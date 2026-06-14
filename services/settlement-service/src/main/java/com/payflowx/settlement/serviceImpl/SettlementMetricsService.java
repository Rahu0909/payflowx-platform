package com.payflowx.settlement.serviceImpl;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementMetricsService {

    private final Counter settlementsCreatedCounter;
    private final Counter settlementsReleasedCounter;
    private final Counter payoutsProcessedCounter;
    private final Counter disputesCreatedCounter;

    public void incrementSettlementCreated() {
        settlementsCreatedCounter.increment();
    }

    public void incrementSettlementReleased() {
        settlementsReleasedCounter.increment();
    }

    public void incrementPayoutProcessed() {
        payoutsProcessedCounter.increment();
    }

    public void incrementDisputeCreated() {
        disputesCreatedCounter.increment();
    }
}