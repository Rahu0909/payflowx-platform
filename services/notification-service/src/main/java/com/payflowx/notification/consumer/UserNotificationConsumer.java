package com.payflowx.notification.consumer;

import com.payflowx.notification.config.RabbitMqConfig;
import com.payflowx.notification.dto.UserNotificationMessage;
import com.payflowx.notification.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserNotificationConsumer {

    private final UserNotificationService userNotificationService;

    @RabbitListener(queues = RabbitMqConfig.USER_NOTIFICATION_QUEUE)
    public void consume(UserNotificationMessage message) {
        log.info("Received user notification event type={} eventId={}", message.eventType(), message.eventId());
        userNotificationService.process(message);
    }
}