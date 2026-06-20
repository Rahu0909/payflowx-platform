package com.payflowx.payment.config;

public final class AuditRabbitMqConstants {

    private AuditRabbitMqConstants() {
    }

    public static final String AUDIT_EXCHANGE = "payflowx.audit.exchange";

    public static final String PAYMENT_AUDIT_ROUTING_KEY = "audit.payment";
}