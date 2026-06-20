package com.payflowx.audit.serviceImpl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class AuditMetricsService {

    private final Counter receivedCounter;
    private final Counter duplicateCounter;
    private final Counter searchCounter;

    public AuditMetricsService(MeterRegistry meterRegistry) {
        this.receivedCounter = meterRegistry.counter("audit_events_received_total");
        this.duplicateCounter = meterRegistry.counter("audit_events_duplicate_total");
        this.searchCounter = meterRegistry.counter("audit_search_requests_total");
    }

    public void incrementReceived() {
        receivedCounter.increment();
    }

    public void incrementDuplicate() {
        duplicateCounter.increment();
    }

    public void incrementSearch() {
        searchCounter.increment();
    }
}