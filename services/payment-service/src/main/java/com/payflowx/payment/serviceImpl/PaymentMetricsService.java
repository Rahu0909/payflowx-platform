package com.payflowx.payment.serviceImpl;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMetricsService {

    private final Counter paymentsProcessedCounter;
    private final Counter paymentsFailedCounter;
    private final Counter paymentRefundCounter;

    public void incrementSuccess() {
        paymentsProcessedCounter.increment();
    }

    public void incrementFailure() {
        paymentsFailedCounter.increment();
    }

    public void incrementRefund() {
        paymentRefundCounter.increment();
    }
}