package com.payflowx.notification.consumer;

import com.payflowx.notification.config.RabbitMqConfig;
import com.payflowx.notification.dto.AuthNotificationMessage;
import com.payflowx.notification.service.AuthNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthNotificationConsumer {

    private final AuthNotificationService authNotificationService;

    @RabbitListener(queues = RabbitMqConfig.AUTH_NOTIFICATION_QUEUE)
    public void consume(AuthNotificationMessage message) {
        log.info("Received auth event type={} eventId={}", message.eventType(), message.eventId());
        authNotificationService.process(message);
    }
}