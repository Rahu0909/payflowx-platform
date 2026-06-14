package com.payflowx.merchant.serviceImpl;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantMetricsService {

    private final Counter merchantsCreatedCounter;
    private final Counter merchantsApprovedCounter;
    private final Counter merchantsRejectedCounter;

    public void incrementCreated() {
        merchantsCreatedCounter.increment();
    }

    public void incrementApproved() {
        merchantsApprovedCounter.increment();
    }

    public void incrementRejected() {
        merchantsRejectedCounter.increment();
    }
}