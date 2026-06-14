package com.payflowx.order.serviceImpl;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMetricsService {

    private final Counter ordersCreatedCounter;
    private final Counter ordersCancelledCounter;

    public void incrementCreated() {
        ordersCreatedCounter.increment();
    }

    public void incrementCancelled() {
        ordersCancelledCounter.increment();
    }
}