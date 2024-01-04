package com.tuto.broker.activemq.service;

import com.tuto.broker.activemq.message.DataFlow;
import com.tuto.broker.activemq.utils.DataFlowGenerate;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Profile("activemq")
public class ActiveMqDataFlowService {
    public static final int BATCH_SIZE = 5000;
    private final JmsOperations jmsTemplates;
    private final Executor executor;

    public ActiveMqDataFlowService(JmsOperations jmsTemplates, Executor executor) {
        this.jmsTemplates = jmsTemplates;
        this.executor = executor;
    }

    public Mono<DataFlow> produceOne() {
        var uuidMessage = UUID.randomUUID().toString();
        var dataFlow =  DataFlowGenerate.generateOneDataFlow();
        log.debug("Sending One Message with JMSCorrelationID : {}", uuidMessage);
        jmsTemplates.convertAndSend("QUEUE_TOPIC", dataFlow,
                msgProcessor -> {
                    msgProcessor.setJMSCorrelationID(uuidMessage);
                    msgProcessor.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 5L);
                    msgProcessor.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 0);
                    return msgProcessor;
                });
        return Mono.justOrEmpty(dataFlow);
    }

    public Mono<Long> produce10K() {
        return produceNkReact(10000);
    }

    public Mono<Long> produce100K() {
        return produceNkReact(100000);
    }

    public Mono<Long> produce1M() {
        return produceNkReact(1000000);
    }

    private Long produceNK(Integer number) {
        AtomicInteger counter = new AtomicInteger();
        streamNDataFlow(number)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement()/BATCH_SIZE))
                .values()
                .parallelStream()
                .map(item -> CompletableFuture.runAsync(() -> computeAsync(item), executor))
                .forEach(CompletableFuture::join);

        return number.longValue();
    }

    private Mono<Long> produceNkReact(Integer number) {
        return Flux.fromIterable(DataFlowGenerate.generateNDataFlow(number))
                .buffer(BATCH_SIZE)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(item -> CompletableFuture.runAsync(() -> computeAsync(item), executor)
                        .thenAccept(result -> log.info("completed one task")))
                .map(List::size)
                .reduce(0L, Long::sum);
    }

    private Stream<DataFlow> streamNDataFlow(Integer number) {
        return DataFlowGenerate.generateNDataFlow(number)
                .stream();
    }


    private void computeAsync(List<DataFlow> dataFlows) {
        dataFlows.forEach(item -> {
            var uuidMessage = UUID.randomUUID().toString();
            log.debug("Sending One Message with JMSCorrelationID : {}", uuidMessage);
            jmsTemplates.convertAndSend("QUEUE_TOPIC", item, msgProcessor -> {
                msgProcessor.setJMSCorrelationID(uuidMessage);
                msgProcessor.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 5L);
                msgProcessor.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 0);
                return msgProcessor;
            });
        });
    }
}
