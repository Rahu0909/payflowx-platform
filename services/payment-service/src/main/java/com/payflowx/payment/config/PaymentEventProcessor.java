package com.payflowx.payment.config;

import com.payflowx.payment.repository.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProcessor {

    private static final int MAX_RETRY_COUNT = 5;
    private final PaymentEventRepository paymentEventRepository;

    @Scheduled(fixedDelay = 30000)
    public void processEvents() {
        var events = paymentEventRepository.findTop100ByProcessedFalseOrderByCreatedAtAsc();
        if (events.isEmpty()) {
            log.debug("No payment events pending");
            return;
        }
        events.forEach(event -> {
            try {
                /*
                 * FUTURE:
                 * Kafka publishing
                 * Webhook delivery
                 * Notification dispatch
                 */
                log.info("Processing payment event id={} type={}", event.getId(), event.getEventType());
                if (event.getRetryCount() >= MAX_RETRY_COUNT) {
                    log.error("Payment event exceeded retry threshold id={}", event.getId());
                    event.setProcessed(true);
                    return;
                }
                event.setProcessed(true);
                event.setProcessedAt(LocalDateTime.now());
            } catch (Exception ex) {
                event.setRetryCount(event.getRetryCount() + 1);
                log.error("Payment event processing failed id={}", event.getId(), ex);
            }
        });
        paymentEventRepository.saveAll(events);
        log.info("Payment events processed count={}", events.size());
    }
}