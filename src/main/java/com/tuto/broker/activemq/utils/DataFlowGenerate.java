package com.tuto.broker.activemq.utils;

import com.tuto.broker.activemq.message.DataFlow;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataFlowGenerate {


    public static DataFlow generateOneDataFlow() {
        Faker faker = new Faker();
        return DataFlow.builder()
                .open(faker.number().randomDouble(2, 90, 200))
                .close(faker.number().randomDouble(2, 90, 200))
                .volume(faker.number().randomDouble(0, 1000, 1000000))
                .splitFactor(faker.number().randomDouble(2, 0, 1))
                .dividend(faker.number().randomDouble(2, 0, 1))
                .symbol(faker.money().currencyCode())
                .exchange(faker.money().currency())
                .date(LocalDateTime.parse(faker.date().future(1, TimeUnit.DAYS, "YYYY-MM-dd HH:mm:ss"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public static List<DataFlow> generateNDataFlow(Integer itemNumber) {
        return IntStream.range(0, itemNumber).mapToObj(item -> generateOneDataFlow()).toList();
    }

    public static List<List<DataFlow>> paritionList(List<DataFlow> dataFlows, Integer chunkSize) {
        AtomicInteger counter = new AtomicInteger();
        return dataFlows.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement()/chunkSize))
                .values().stream().toList();
    }
}
