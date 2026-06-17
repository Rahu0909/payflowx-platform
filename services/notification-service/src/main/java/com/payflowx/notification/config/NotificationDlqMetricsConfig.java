package com.payflowx.notification.config;

import com.payflowx.notification.enums.WebhookDeliveryStatus;
import com.payflowx.notification.repository.WebhookDeliveryRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class NotificationDlqMetricsConfig {

    private final WebhookDeliveryRepository repository;

    @Bean
    public Gauge deadLetterGauge(MeterRegistry registry) {
        return Gauge.builder("notification_dead_letter_current", () -> repository.countByStatus(WebhookDeliveryStatus.DEAD_LETTER)).register(registry);
    }

    @Bean
    public Gauge retryingGauge(MeterRegistry registry) {
        return Gauge.builder("notification_retrying_current", () -> repository.countByStatus(WebhookDeliveryStatus.RETRYING)).register(registry);
    }
}