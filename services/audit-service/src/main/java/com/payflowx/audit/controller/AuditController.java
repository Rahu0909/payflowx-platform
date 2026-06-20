package com.payflowx.audit.controller;

import com.payflowx.audit.dto.AuditEventResponse;
import com.payflowx.audit.dto.AuditSearchRequest;
import com.payflowx.audit.dto.AuditStatisticsResponse;
import com.payflowx.audit.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditEventService auditEventService;

    @GetMapping
    public Page<AuditEventResponse> searchAuditEvents(
            @RequestParam(required = false) String sourceService,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false) String aggregateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        AuditSearchRequest request = AuditSearchRequest.builder().sourceService(sourceService).eventType(eventType).correlationId(correlationId).aggregateId(aggregateId).build();
        return auditEventService.searchAuditEvents(request, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/{eventId}")
    public AuditEventResponse getAuditEvent(@PathVariable String eventId) {
        return auditEventService.getAuditEvent(eventId);
    }

    @GetMapping("/statistics")
    public AuditStatisticsResponse statistics() {
        return auditEventService.getStatistics();
    }
}