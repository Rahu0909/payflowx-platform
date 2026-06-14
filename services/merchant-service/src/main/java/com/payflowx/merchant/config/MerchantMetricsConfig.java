package com.payflowx.merchant.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MerchantMetricsConfig {

    @Bean
    public Counter merchantsCreatedCounter(MeterRegistry registry) {
        return Counter.builder("merchants_created_total")
                .description("Total merchants created")
                .register(registry);
    }

    @Bean
    public Counter merchantsApprovedCounter(MeterRegistry registry) {
        return Counter.builder("merchants_approved_total")
                .description("Total merchants approved")
                .register(registry);
    }

    @Bean
    public Counter merchantsRejectedCounter(MeterRegistry registry) {
        return Counter.builder("merchants_rejected_total")
                .description("Total merchants rejected")
                .register(registry);
    }
}