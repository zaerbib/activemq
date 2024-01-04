package com.tuto.broker.activemq.message;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DataFlow {
    private Double open;
    private Double close;
    private Double volume;
    private Double splitFactor;
    private Double dividend;
    private String symbol;
    private String exchange;
    private LocalDateTime date;
}
