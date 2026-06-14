package com.payflowx.user.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserMetricsConfig {

    @Bean
    public Counter usersCreatedCounter(MeterRegistry registry) {
        return Counter.builder("users_created_total")
                .description("Total users created")
                .register(registry);
    }

    @Bean
    public Counter kycSubmittedCounter(MeterRegistry registry) {
        return Counter.builder("kyc_submitted_total")
                .description("Total KYC submitted")
                .register(registry);
    }

    @Bean
    public Counter kycApprovedCounter(MeterRegistry registry) {
        return Counter.builder("kyc_approved_total")
                .description("Total KYC approved")
                .register(registry);
    }

    @Bean
    public Counter usersBlockedCounter(MeterRegistry registry) {
        return Counter.builder("users_blocked_total")
                .description("Total blocked users")
                .register(registry);
    }
}