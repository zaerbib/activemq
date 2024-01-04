package com.tuto.broker.activemq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tuto.broker.activemq.message.DataFlow;
import com.tuto.broker.activemq.utils.config.ActiveMqProperties;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Profile("activemq")
@Configuration
public class ActiveMqConfig {

    private final ActiveMqProperties activeMqProperties;
    private final ObjectMapper objectMapper;

    public ActiveMqConfig(ActiveMqProperties activeMqProperties, ObjectMapper objectMapper) {
        this.activeMqProperties = activeMqProperties;
        this.objectMapper = objectMapper;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                activeMqProperties.getUser(),
                activeMqProperties.getPassword(),
                activeMqProperties.getBrokerUrl()
        );

        connectionFactory.setTrustedPackages(Collections.singletonList(DataFlow.class.getPackageName()));
        return connectionFactory;
    }

    @Bean
    public JmsOperations jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setMessageConverter(messageConverter(objectMapper));
        return jmsTemplate;
    }

    public MappingJackson2MessageConverter messageConverter(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("message_type");
        converter.setObjectMapper(objectMapper);

        Map<String, Class<?>> typeIdMap = new HashMap<>();
        typeIdMap.put("dataFlow", DataFlow.class);
        converter.setTypeIdMappings(typeIdMap);

        return converter;
    }
}
