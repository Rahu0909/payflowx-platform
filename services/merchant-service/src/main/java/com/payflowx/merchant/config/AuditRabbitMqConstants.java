package com.payflowx.merchant.config;

public final class AuditRabbitMqConstants {

    private AuditRabbitMqConstants() {}

    public static final String AUDIT_EXCHANGE =
            "payflowx.audit.exchange";

    public static final String MERCHANT_AUDIT_ROUTING_KEY =
            "audit.merchant";
}