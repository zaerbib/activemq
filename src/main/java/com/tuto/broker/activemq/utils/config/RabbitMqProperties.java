package com.tuto.broker.activemq.utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Data
public class RabbitMqProperties {
    private String host;
    private Integer port;
    private String username;
    private String password;
}
