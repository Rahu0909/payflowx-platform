package com.payflowx.settlement.scheduler;

import com.payflowx.settlement.repository.SettlementWebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementWebhookProcessor {
    private final SettlementWebhookEventRepository repository;

    @Scheduled(fixedDelay = 30000)
    public void processWebhookEvents() {
        var events = repository.findTop100ByProcessedFalseOrderByCreatedAtAsc();
        if (events.isEmpty()) {
            log.debug("No settlement webhook events pending");
            return;
        }
        events.forEach(event -> {
            try {
                /*
                 * FUTURE:
                 * actual HTTP delivery
                 */
                log.info("Processing settlement webhook id={} type={}", event.getId(), event.getEventType());
                event.setProcessed(true);
                event.setProcessedAt(LocalDateTime.now());
            } catch (Exception ex) {
                event.setRetryCount(event.getRetryCount() + 1);
                log.error("Settlement webhook processing failed id={}", event.getId(), ex);
            }
        });
        repository.saveAll(events);
        log.info("Settlement webhook events processed count={}", events.size());
    }
}