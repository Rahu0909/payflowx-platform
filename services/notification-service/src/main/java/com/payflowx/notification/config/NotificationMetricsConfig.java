package com.payflowx.notification.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationMetricsConfig {

    @Bean
    public Counter notificationsSentCounter(MeterRegistry registry) {
        return Counter.builder("notifications_sent_total")
                .description("Total notifications sent")
                .register(registry);
    }

    @Bean
    public Counter notificationsFailedCounter(MeterRegistry registry) {
        return Counter.builder("notifications_failed_total")
                .description("Total notifications failed")
                .register(registry);
    }

    @Bean
    public Counter emailNotificationsCounter(MeterRegistry registry) {
        return Counter.builder("email_notifications_total")
                .description("Total email notifications")
                .register(registry);
    }

    @Bean
    public Counter webhookNotificationsCounter(MeterRegistry registry) {
        return Counter.builder("webhook_notifications_total")
                .description("Total webhook notifications")
                .register(registry);
    }

    @Bean
    public Counter webhookRetryCounter(MeterRegistry registry) {
        return Counter.builder("webhook_retry_total")
                .description("Webhook retries")
                .register(registry);
    }

    @Bean
    public Counter webhookDeadLetterCounter(MeterRegistry registry) {
        return Counter.builder("webhook_dead_letter_total")
                .description("Webhook dead letter events")
                .register(registry);
    }
}