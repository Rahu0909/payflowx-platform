package com.payflowx.order.config;

public final class AuditRabbitMqConstants {

    private AuditRabbitMqConstants() {}

    public static final String AUDIT_EXCHANGE = "payflowx.audit.exchange";

    public static final String ORDER_AUDIT_ROUTING_KEY = "audit.order";
}