package com.payflowx.audit.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange auditExchange() {
        return new TopicExchange(AuditRabbitMqConstants.AUDIT_EXCHANGE);
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(AuditRabbitMqConstants.AUDIT_QUEUE).build();
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange auditExchange) {
        return BindingBuilder.bind(auditQueue).to(auditExchange).with(AuditRabbitMqConstants.AUDIT_ROUTING_KEY);
    }
}