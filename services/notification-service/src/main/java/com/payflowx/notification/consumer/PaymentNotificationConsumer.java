package com.payflowx.notification.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payflowx.notification.config.RabbitMqConfig;
import com.payflowx.notification.dto.PaymentNotificationMessage;
import com.payflowx.notification.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentNotificationConsumer {
    private final NotificationEventService notificationEventService;

    @RabbitListener(queues = RabbitMqConfig.PAYMENT_NOTIFICATION_QUEUE)
    public void consumePaymentNotification(
            Map<String, Object> payload,
            @Header(name = "X-PAYFLOWX-EVENT-ID") String eventId,
            @Header(name = "X-PAYFLOWX-CORRELATION-ID") String correlationId,
            @Header(name = "X-PAYFLOWX-MERCHANT-ID") String merchantId,
            @Header(name = "X-PAYFLOWX-EVENT-TYPE") String eventType,
            @Header(name = "X-PAYFLOWX-SOURCE-SERVICE", required = false) String sourceService) throws JsonProcessingException {
        log.info("Payment notification received eventId={} merchantId={} eventType={}", eventId, merchantId, eventType);
        PaymentNotificationMessage message = new PaymentNotificationMessage(eventId, correlationId, merchantId, sourceService == null || sourceService.isBlank() ? "payment-service" : sourceService, eventType, payload);
        notificationEventService.processPaymentNotification(message);
    }
}