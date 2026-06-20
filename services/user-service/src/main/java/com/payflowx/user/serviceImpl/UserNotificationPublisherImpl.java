package com.payflowx.user.serviceImpl;

import com.payflowx.user.config.AuditRabbitMqConstants;
import com.payflowx.user.dto.AuditEventMessage;
import com.payflowx.user.dto.event.UserNotificationEvent;
import com.payflowx.user.service.UserNotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationPublisherImpl implements UserNotificationPublisher {
    private static final String NOTIFICATION_EXCHANGE = "payflowx.notification.exchange";
    private static final Map<String, String> ROUTING_KEYS = Map.of("USER_CREATED", "user.created", "USER_KYC_SUBMITTED", "user.kyc.submitted", "USER_KYC_APPROVED", "user.kyc.approved", "USER_KYC_REJECTED", "user.kyc.rejected", "USER_ACCOUNT_BLOCKED", "user.account.blocked", "USER_ACCOUNT_SUSPENDED", "user.account.suspended");
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(UserNotificationEvent event) {
        String routingKey = ROUTING_KEYS.get(event.getEventType());
        if (routingKey == null) {
            log.error("Unsupported user eventType={}", event.getEventType());
            return;
        }
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, routingKey, event, message -> {
            message.getMessageProperties().setHeader("X-PAYFLOWX-CORRELATION-ID", MDC.get("correlationId"));
            return message;
        });
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "CORR-" + event.getUserId();
        }
        AuditEventMessage auditEvent =
                new AuditEventMessage(
                        java.util.UUID.randomUUID().toString(),
                        correlationId,
                        event.getUserId().toString(),
                        "user-service",
                        event.getEventType(),
                        event
                );
        rabbitTemplate.convertAndSend(
                AuditRabbitMqConstants.AUDIT_EXCHANGE,
                AuditRabbitMqConstants.USER_AUDIT_ROUTING_KEY,
                auditEvent
        );
        log.info("User notification event published eventType={} userId={}", event.getEventType(), event.getUserId());
    }
}