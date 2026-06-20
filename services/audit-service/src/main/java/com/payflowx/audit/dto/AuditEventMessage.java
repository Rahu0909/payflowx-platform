package com.payflowx.audit.dto;

public record AuditEventMessage(

        String eventId,

        String correlationId,

        String aggregateId,

        String sourceService,

        String eventType,

        Object payload

) {
}