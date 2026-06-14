package com.payflowx.settlement.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SettlementMetricsConfig {

    @Bean
    public Counter settlementsCreatedCounter(MeterRegistry registry) {
        return Counter.builder("settlements_created_total")
                .description("Total settlements created")
                .register(registry);
    }

    @Bean
    public Counter settlementsReleasedCounter(MeterRegistry registry) {
        return Counter.builder("settlements_released_total")
                .description("Total settlements released")
                .register(registry);
    }

    @Bean
    public Counter payoutsProcessedCounter(MeterRegistry registry) {
        return Counter.builder("payouts_processed_total")
                .description("Total payouts processed")
                .register(registry);
    }

    @Bean
    public Counter disputesCreatedCounter(MeterRegistry registry) {
        return Counter.builder("disputes_created_total")
                .description("Total disputes created")
                .register(registry);
    }
}