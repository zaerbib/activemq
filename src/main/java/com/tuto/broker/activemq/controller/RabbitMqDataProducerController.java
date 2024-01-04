package com.tuto.broker.activemq.controller;

import com.tuto.broker.activemq.message.DataFlow;
import com.tuto.broker.activemq.service.RabbitMqDataFlowService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Profile("rabbitMq")
@RestController
@RequestMapping("/rabbitmq/produce")
public class RabbitMqDataProducerController {

    private final RabbitMqDataFlowService service;

    public RabbitMqDataProducerController(RabbitMqDataFlowService service) {
        this.service = service;
    }

    @GetMapping("one")
    public Mono<DataFlow> produceOne() {
        return service.produceOne();
    }

    @GetMapping("10K")
    public Mono<Long> produce10K() {
        return service.produce10K();
    }

    @GetMapping("100K")
    public Mono<Long> produce100K() {
        return service.produce100K();
    }

    @GetMapping("1M")
    public Mono<Long> produce1M() {
        return service.produce1M();
    }
}
