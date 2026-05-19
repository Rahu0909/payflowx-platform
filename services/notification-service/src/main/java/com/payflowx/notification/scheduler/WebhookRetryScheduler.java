package com.payflowx.notification.scheduler;

import com.payflowx.notification.entity.NotificationEvent;
import com.payflowx.notification.entity.WebhookDelivery;
import com.payflowx.notification.enums.WebhookDeliveryStatus;
import com.payflowx.notification.repository.NotificationEventRepository;
import com.payflowx.notification.repository.WebhookDeliveryRepository;
import com.payflowx.notification.service.WebhookDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookRetryScheduler {
    private final WebhookDeliveryRepository webhookDeliveryRepository;
    private final NotificationEventRepository notificationEventRepository;
    private final WebhookDispatcherService webhookDispatcherService;

    @Scheduled(fixedDelay = 60000)
    public void retryFailedWebhooks() {
        log.info("Webhook retry scheduler started");
        List<WebhookDelivery> retryableDeliveries = webhookDeliveryRepository.findByStatusAndNextRetryAtBefore
                (WebhookDeliveryStatus.RETRYING, LocalDateTime.now());
        log.info("Retryable webhook count={}", retryableDeliveries.size());
        for (WebhookDelivery delivery : retryableDeliveries) {
            try {
                NotificationEvent event = notificationEventRepository.findByEventId(delivery.getEventId()).orElseThrow();
                log.info("Retrying webhook eventId={} retryCount={}", delivery.getEventId(), delivery.getRetryCount());
                webhookDispatcherService.dispatchWebhook(event);
            } catch (Exception ex) {
                log.error("Webhook retry failed eventId={}", delivery.getEventId(), ex);
            }
        }
    }
}