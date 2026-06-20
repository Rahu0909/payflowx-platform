package com.payflowx.audit.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditMetricsConfig {

    public AuditMetricsConfig(MeterRegistry registry) {
        registry.config().commonTags("service", "audit-service");
    }
}