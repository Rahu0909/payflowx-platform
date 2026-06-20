package com.payflowx.audit.service;

import com.payflowx.audit.dto.AuditEventMessage;
import com.payflowx.audit.dto.AuditEventResponse;
import com.payflowx.audit.dto.AuditSearchRequest;
import com.payflowx.audit.dto.AuditStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditEventService {

    void saveEvent(AuditEventMessage message);

    Page<AuditEventResponse> searchAuditEvents(AuditSearchRequest request, Pageable pageable);

    AuditEventResponse getAuditEvent(String eventId);

    AuditStatisticsResponse getStatistics();
}