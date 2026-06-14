package com.payflowx.order.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMetricsConfig {

    @Bean
    public Counter ordersCreatedCounter(MeterRegistry registry) {
        return Counter.builder("orders_created_total")
                .description("Total orders created")
                .register(registry);
    }

    @Bean
    public Counter ordersCancelledCounter(MeterRegistry registry) {
        return Counter.builder("orders_cancelled_total")
                .description("Total orders cancelled")
                .register(registry);
    }
}