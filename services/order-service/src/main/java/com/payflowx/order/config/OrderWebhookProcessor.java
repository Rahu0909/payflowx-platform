package com.payflowx.order.config;

import com.payflowx.order.repository.OrderWebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderWebhookProcessor {
    private final OrderWebhookEventRepository repository;

    @Scheduled(fixedDelay = 30000)
    public void processWebhookEvents() {
        var events = repository.findTop100ByProcessedFalseOrderByCreatedAtAsc();
        if (events.isEmpty()) {
            log.debug("No webhook events pending");
            return;
        }

        events.forEach(event -> {
            try {
                /*
                 * FUTURE:
                 * Actual HTTP webhook delivery
                 */
                log.info("Processing webhook event id={} type={}", event.getId(), event.getEventType());
                event.setProcessed(true);
                event.setProcessedAt(LocalDateTime.now());
            } catch (Exception ex) {
                event.setRetryCount(event.getRetryCount() + 1);
                log.error("Webhook processing failed id={}", event.getId(), ex);
            }
        });
        repository.saveAll(events);
        log.info("Webhook events processed count={}", events.size());
    }
}