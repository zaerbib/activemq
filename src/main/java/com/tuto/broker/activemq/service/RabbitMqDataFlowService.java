package com.tuto.broker.activemq.service;

import com.tuto.broker.activemq.message.DataFlow;
import com.tuto.broker.activemq.utils.DataFlowGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Profile("rabbitMq")
@Service
@Slf4j
public class RabbitMqDataFlowService {

    public static final int BATCH_SIZE = 5000;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate template;
    private final Executor executor;

    public RabbitMqDataFlowService(RabbitTemplate template, Executor executor) {
        this.template = template;
        this.executor = executor;
    }

    public Mono<DataFlow> produceOne() {
        var uuidMessage = UUID.randomUUID().toString();
        var dataFlow =  DataFlowGenerate.generateOneDataFlow();
        log.debug("Sending One Message with JMSCorrelationID : {}", uuidMessage);
        template.convertAndSend(exchange, routingKey, dataFlow, message -> {
            MessageProperties mesProp = message.getMessageProperties();
            mesProp.setCorrelationId(uuidMessage);
            return message;
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

    private Mono<Long> produceNkReact(Integer number) {
        return Flux.fromIterable(DataFlowGenerate.generateNDataFlow(number))
                .buffer(BATCH_SIZE)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(item -> CompletableFuture.runAsync(() -> computeAsync(item), executor)
                        .thenAccept(result -> log.info("completed one task")))
                .map(List::size)
                .reduce(0L, Long::sum);
    }

    private void computeAsync(List<DataFlow> dataFlows) {
        dataFlows.forEach(item -> {
            var uuidMessage = UUID.randomUUID().toString();
            log.debug("Sending One Message with JMSCorrelationID : {}", uuidMessage);
            template.convertAndSend(exchange, routingKey, item, message -> {
                MessageProperties mesProp = message.getMessageProperties();
                mesProp.setCorrelationId(uuidMessage);
                return message;
            });
        });
    }
}
