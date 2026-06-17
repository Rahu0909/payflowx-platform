package com.payflowx.notification.serviceImpl;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationMetricsService {

    private final Counter notificationsSentCounter;
    private final Counter notificationsFailedCounter;
    private final Counter emailNotificationsCounter;
    private final Counter webhookNotificationsCounter;
    private final Counter webhookRetryCounter;
    private final Counter webhookDeadLetterCounter;

    public void incrementSent() {
        notificationsSentCounter.increment();
    }

    public void incrementFailed() {
        notificationsFailedCounter.increment();
    }

    public void incrementEmail() {
        emailNotificationsCounter.increment();
    }

    public void incrementWebhook() {
        webhookNotificationsCounter.increment();
    }

    public void incrementRetry() {
        webhookRetryCounter.increment();
    }

    public void incrementDeadLetter() {
        webhookDeadLetterCounter.increment();
    }
}