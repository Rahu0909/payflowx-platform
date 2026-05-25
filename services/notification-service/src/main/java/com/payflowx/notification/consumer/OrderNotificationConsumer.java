package com.payflowx.notification.consumer;

import com.payflowx.notification.config.RabbitMqConfig;
import com.payflowx.notification.dto.OrderNotificationMessage;
import com.payflowx.notification.service.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderNotificationConsumer {
    private final OrderNotificationService orderNotificationService;

    @RabbitListener(queues = RabbitMqConfig.ORDER_NOTIFICATION_QUEUE)
    public void consume(OrderNotificationMessage message) {
        log.info("Received order notification event type={} orderId={}", message.eventType(), message.orderId());
        orderNotificationService.process(message);
    }
}