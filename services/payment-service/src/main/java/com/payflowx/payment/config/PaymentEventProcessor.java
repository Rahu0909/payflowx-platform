package com.payflowx.payment.config;

import com.payflowx.payment.entity.PaymentEvent;
import com.payflowx.payment.enums.PaymentEventType;
import com.payflowx.payment.repository.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProcessor {
    private static final int MAX_RETRY_COUNT = 5;
    private final PaymentEventRepository paymentEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 30000)
    public void processEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<PaymentEvent> events = paymentEventRepository.findTop100ByProcessedFalseOrderByCreatedAtAsc();
        if (events.isEmpty()) {
            log.debug("No payment events pending");
            return;
        }
        for (PaymentEvent event : events) {
            if (event.getNextRetryAt() != null && event.getNextRetryAt().isAfter(now)) {
                continue;
            }
            try {
                String routingKey = resolveRoutingKey(event.getEventType());
                rabbitTemplate.convertAndSend(PaymentNotificationRabbitMqConstants.NOTIFICATION_EXCHANGE, routingKey, event.getPayload(), message -> {
                    MessageProperties props = message.getMessageProperties();
                    props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    props.setContentEncoding("UTF-8");
                    props.setMessageId(event.getId().toString());
                    props.setCorrelationId(event.getPaymentId().toString());
                    props.setHeader("X-PAYFLOWX-EVENT-ID", event.getId().toString());
                    props.setHeader("X-PAYFLOWX-CORRELATION-ID", event.getPaymentId().toString());
                    props.setHeader("X-PAYFLOWX-MERCHANT-ID", event.getMerchantId().toString());
                    props.setHeader("X-PAYFLOWX-EVENT-TYPE", event.getEventType().name());
                    props.setHeader("X-PAYFLOWX-SOURCE-SERVICE", PaymentNotificationRabbitMqConstants.SOURCE_SERVICE);
                    return message;
                });
                event.setProcessed(true);
                event.setProcessedAt(now);
                event.setNextRetryAt(null);
                log.info("Payment event published eventId={} paymentId={} eventType={}", event.getId(), event.getPaymentId(), event.getEventType());
            } catch (Exception ex) {
                int nextRetryCount = event.getRetryCount() + 1;
                event.setRetryCount(nextRetryCount);
                if (nextRetryCount >= MAX_RETRY_COUNT) {
                    event.setProcessed(true);
                    event.setProcessedAt(now);
                    log.error("Payment event exceeded retry threshold eventId={} paymentId={} eventType={}", event.getId(), event.getPaymentId(), event.getEventType(), ex);
                } else {
                    long delayMinutes = calculateBackoffMinutes(nextRetryCount);
                    event.setNextRetryAt(now.plusMinutes(delayMinutes));
                    log.error("Payment event publish failed eventId={} paymentId={} eventType={} retryCount={} nextRetryAt={}", event.getId(), event.getPaymentId(), event.getEventType(), nextRetryCount, event.getNextRetryAt(), ex);
                }
            }
        }
        paymentEventRepository.saveAll(events);
        log.info("Payment events processed count={}", events.size());
    }

    private String resolveRoutingKey(PaymentEventType eventType) {
        return switch (eventType) {
            case PAYMENT_CREATED -> PaymentNotificationRabbitMqConstants.PAYMENT_CREATED_ROUTING_KEY;
            case PAYMENT_PROCESSING -> PaymentNotificationRabbitMqConstants.PAYMENT_PROCESSING_ROUTING_KEY;
            case PAYMENT_SUCCESS -> PaymentNotificationRabbitMqConstants.PAYMENT_SUCCESS_ROUTING_KEY;
            case PAYMENT_FAILED -> PaymentNotificationRabbitMqConstants.PAYMENT_FAILED_ROUTING_KEY;
            case REFUND_CREATED -> PaymentNotificationRabbitMqConstants.REFUND_CREATED_ROUTING_KEY;
            case REFUND_SUCCESS -> PaymentNotificationRabbitMqConstants.REFUND_SUCCESS_ROUTING_KEY;
            case REFUND_FAILED -> PaymentNotificationRabbitMqConstants.REFUND_FAILED_ROUTING_KEY;
        };
    }

    private long calculateBackoffMinutes(int retryCount) {
        long delay = (long) Math.pow(2, Math.max(1, retryCount));
        return Math.min(delay, 60L);
    }
}