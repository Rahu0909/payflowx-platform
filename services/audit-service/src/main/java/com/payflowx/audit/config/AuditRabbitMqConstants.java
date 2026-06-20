package com.payflowx.audit.config;

public final class AuditRabbitMqConstants {

    private AuditRabbitMqConstants() {
    }

    public static final String AUDIT_EXCHANGE = "payflowx.audit.exchange";

    public static final String AUDIT_QUEUE = "payflowx.audit.queue";

    public static final String AUDIT_ROUTING_KEY = "audit.#";
}