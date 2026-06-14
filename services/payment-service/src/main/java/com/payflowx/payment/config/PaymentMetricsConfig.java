package com.payflowx.payment.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PaymentMetricsConfig {

    @Bean
    public Counter paymentsProcessedCounter(MeterRegistry registry) {
        return Counter.builder("payments_processed_total").description("Total successful payments").register(registry);
    }

    @Bean
    public Counter paymentsFailedCounter(MeterRegistry registry) {
        return Counter.builder("payments_failed_total").description("Total failed payments").register(registry);
    }

    @Bean
    public Counter paymentRefundCounter(MeterRegistry registry) {
        return Counter.builder("payments_refunded_total").description("Total refunded payments").register(registry);
    }
}