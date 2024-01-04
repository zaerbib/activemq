package com.tuto.broker.activemq.utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.activemq")
@Data
public class ActiveMqProperties {
    private String brokerUrl;
    private String user;
    private String password;
}
