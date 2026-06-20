package com.payflowx.user.dto;

import java.io.Serializable;

public record AuditEventMessage(

        String eventId,

        String correlationId,

        String aggregateId,

        String sourceService,

        String eventType,

        Object payload

) implements Serializable {
}