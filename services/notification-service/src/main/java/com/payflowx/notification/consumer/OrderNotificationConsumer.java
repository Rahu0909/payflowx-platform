package com.payflowx.notification.consumer;

import com.payflowx.notification.config.RabbitMqConfig;
import com.payflowx.notification.dto.OrderNotificationMessage;
import com.payflowx.notification.service.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderNotificationConsumer {
    private final OrderNotificationService orderNotificationService;

    @RabbitListener(queues = RabbitMqConfig.ORDER_NOTIFICATION_QUEUE)
    public void consume(OrderNotificationMessage message, @Header(name = "X-PAYFLOWX-CORRELATION-ID", required = false) String correlationId) {
        try {
            if (correlationId != null) {
                MDC.put("correlationId", correlationId);
            }
            log.info("Received order notification event type={} orderId={}", message.eventType(), message.orderId());
            orderNotificationService.process(message);
        } finally {
            MDC.clear();
        }
    }
}