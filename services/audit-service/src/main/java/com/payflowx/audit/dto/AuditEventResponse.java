package com.payflowx.audit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AuditEventResponse(

        UUID id,

        String eventId,

        String correlationId,

        String aggregateId,

        String sourceService,

        String eventType,

        String payload,

        LocalDateTime createdAt

) {
}