package com.payflowx.settlement.config;

public final class AuditRabbitMqConstants {

    private AuditRabbitMqConstants() {
    }

    public static final String AUDIT_EXCHANGE = "payflowx.audit.exchange";

    public static final String SETTLEMENT_AUDIT_ROUTING_KEY = "audit.settlement";
}