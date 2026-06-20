package com.payflowx.audit.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuditSearchRequest(

        String sourceService,

        String eventType,

        String correlationId,

        String aggregateId,

        LocalDateTime fromDate,

        LocalDateTime toDate

) {
}