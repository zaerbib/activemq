package com.tuto.broker.activemq.controller;

import com.tuto.broker.activemq.message.DataFlow;
import com.tuto.broker.activemq.service.ActiveMqDataFlowService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Profile("activemq")
@RestController
@RequestMapping("/activemq/produce")
public class ActiveMqDataProducerController {

    private final ActiveMqDataFlowService activeMqDataFlowService;


    public ActiveMqDataProducerController(ActiveMqDataFlowService activeMqDataFlowService) {
        this.activeMqDataFlowService = activeMqDataFlowService;
    }

    @GetMapping("one")
    public Mono<DataFlow> produceOne() {
        return activeMqDataFlowService.produceOne();
    }

    @GetMapping("10K")
    public Mono<Long> produce10K() {
        return activeMqDataFlowService.produce10K();
    }

    @GetMapping("100K")
    public Mono<Long> produce100K() {
        return activeMqDataFlowService.produce100K();
    }

    @GetMapping("1M")
    public Mono<Long> produce1M() {
        return activeMqDataFlowService.produce1M();
    }
}
