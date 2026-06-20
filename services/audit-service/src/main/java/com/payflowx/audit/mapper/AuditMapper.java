package com.payflowx.audit.mapper;

import com.payflowx.audit.dto.AuditEventResponse;
import com.payflowx.audit.entity.AuditEvent;
import org.springframework.stereotype.Component;

@Component
public class AuditMapper {

    public AuditEventResponse toResponse(AuditEvent event) {
        return AuditEventResponse.builder()
                .id(event.getId())
                .eventId(event.getEventId())
                .correlationId(event.getCorrelationId())
                .aggregateId(event.getAggregateId())
                .sourceService(event.getSourceService())
                .eventType(event.getEventType())
                .payload(event.getPayload())
                .createdAt(event.getCreatedAt())
                .build();
    }
}