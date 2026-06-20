package com.payflowx.order.serviceImpl;

import com.payflowx.order.config.AuditRabbitMqConstants;
import com.payflowx.order.dto.AuditEventMessage;
import com.payflowx.order.dto.event.OrderNotificationEvent;
import com.payflowx.order.exception.NotificationPublishException;
import com.payflowx.order.service.OrderNotificationPublisher;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationPublisherImpl implements OrderNotificationPublisher {
    private static final String NOTIFICATION_EXCHANGE = "payflowx.notification.exchange";
    private static final Map<String, String> ROUTING_KEYS = Map.of("ORDER_CREATED", "order.created", "ORDER_CANCELLED", "order.cancelled", "ORDER_PAID", "order.paid", "ORDER_EXPIRED", "order.expired");
    private final RabbitTemplate rabbitTemplate;

    @Retry(name = "rabbitPublisher", fallbackMethod = "publishFallback")
    @CircuitBreaker(name = "rabbitPublisher", fallbackMethod = "publishFallback")
    @Override
    public void publish(OrderNotificationEvent event) {
        String routingKey = ROUTING_KEYS.get(event.getEventType());
        if (routingKey == null) {
            log.error("Unsupported order eventType={}", event.getEventType());
            return;
        }
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, routingKey, event, message -> {
            message.getMessageProperties().setHeader("X-PAYFLOWX-CORRELATION-ID", MDC.get("correlationId"));
            return message;
        });
        String correlationId = MDC.get("correlationId");

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "CORR-" + event.getOrderId();
        }

        AuditEventMessage auditEvent =
                new AuditEventMessage(
                        java.util.UUID.randomUUID().toString(),
                        correlationId,
                        event.getOrderId().toString(),
                        "order-service",
                        event.getEventType(),
                        event
                );
        rabbitTemplate.convertAndSend(
                AuditRabbitMqConstants.AUDIT_EXCHANGE,
                AuditRabbitMqConstants.ORDER_AUDIT_ROUTING_KEY,
                auditEvent
        );
        log.info("Order notification event published eventType={} orderId={}", event.getEventType(), event.getOrderId());
    }

    public void publishFallback(OrderNotificationEvent event, Exception ex) {
        log.error("Failed publishing notification orderId={}", event.getOrderId(), ex);
        throw new NotificationPublishException("Notification publish failed", ex);
    }
}