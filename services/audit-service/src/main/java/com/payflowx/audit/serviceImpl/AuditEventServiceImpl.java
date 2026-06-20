package com.payflowx.audit.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.audit.dto.AuditEventMessage;
import com.payflowx.audit.dto.AuditEventResponse;
import com.payflowx.audit.dto.AuditSearchRequest;
import com.payflowx.audit.dto.AuditStatisticsResponse;
import com.payflowx.audit.entity.AuditEvent;
import com.payflowx.audit.exception.ResourceNotFoundException;
import com.payflowx.audit.mapper.AuditMapper;
import com.payflowx.audit.repository.AuditEventRepository;
import com.payflowx.audit.service.AuditEventService;
import com.payflowx.audit.util.AuditSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventServiceImpl implements AuditEventService {
    private final AuditEventRepository repository;
    private final ObjectMapper objectMapper;
    private final AuditMapper auditMapper;
    private final AuditMetricsService metricsService;

    @Override
    public void saveEvent(AuditEventMessage message) {
        metricsService.incrementReceived();
        if (repository.existsByEventId(message.eventId())) {
            metricsService.incrementDuplicate();
            log.warn("Duplicate audit event ignored eventId={}", message.eventId());
            return;
        }
        AuditEvent event = new AuditEvent();
        event.setEventId(message.eventId());
        event.setCorrelationId(message.correlationId());
        event.setAggregateId(message.aggregateId());
        event.setSourceService(message.sourceService());
        event.setEventType(message.eventType());
        try {
            event.setPayload(objectMapper.writeValueAsString(message.payload()));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to serialize audit payload", ex);
        }
        repository.save(event);
        log.info("Audit event persisted eventId={} eventType={}", event.getEventId(), event.getEventType());
    }

    @Override
    public Page<AuditEventResponse> searchAuditEvents(AuditSearchRequest request, Pageable pageable) {
        metricsService.incrementSearch();
        return repository.findAll(AuditSpecification.search(request), pageable).map(auditMapper::toResponse);
    }

    @Override
    public AuditEventResponse getAuditEvent(String eventId) {
        AuditEvent event = repository.findByEventId(eventId).orElseThrow(() -> new ResourceNotFoundException("Audit event not found : " + eventId));
        return auditMapper.toResponse(event);
    }

    @Override
    public AuditStatisticsResponse getStatistics() {
        return AuditStatisticsResponse.builder().totalEvents(repository.count()).
                paymentEvents(repository.countBySourceService("payment-service")).
                settlementEvents(repository.countBySourceService("settlement-service")).
                merchantEvents(repository.countBySourceService("merchant-service")).
                authEvents(repository.countBySourceService("auth-service")).
                userEvents(repository.countBySourceService("user-service")).
                orderEvents(repository.countBySourceService("order-service")).
                notificationEvents(repository.countBySourceService("notification-service")).build();
    }
}