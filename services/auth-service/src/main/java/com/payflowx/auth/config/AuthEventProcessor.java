package com.payflowx.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.auth.dto.AuthNotificationMessage;
import com.payflowx.auth.entity.AuthEvent;
import com.payflowx.auth.enums.AuthEventType;
import com.payflowx.auth.repository.AuthEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventProcessor {
    private static final int MAX_RETRY_COUNT = 5;
    private final AuthEventRepository authEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 30000)
    public void processEvents() {
        List<AuthEvent> events = authEventRepository.findTop100ByProcessedFalseOrderByCreatedAtAsc();
        for (AuthEvent event : events) {
            try {
                String routingKey = resolveRoutingKey(event.getEventType());
                AuthNotificationMessage message = new AuthNotificationMessage(event.getId(), event.getEventType().name(), "auth-service", objectMapper.readValue(event.getPayload(), Map.class));
                rabbitTemplate.convertAndSend(AuthNotificationRabbitMqConstants.NOTIFICATION_EXCHANGE, routingKey, message, rabbitMessage -> {
                    MessageProperties props = rabbitMessage.getMessageProperties();
                    props.setMessageId(event.getId().toString());
                    props.setHeader("X-PAYFLOWX-EVENT-ID", event.getId().toString());
                    props.setHeader("X-PAYFLOWX-CORRELATION-ID", event.getCorrelationId());
                    props.setHeader("X-PAYFLOWX-SOURCE-SERVICE", "auth-service");
                    return rabbitMessage;
                });
                event.setProcessed(true);
                log.info("Published auth event eventId={} type={} correlationId={}", event.getId(), event.getEventType(), event.getCorrelationId());
            } catch (Exception ex) {
                log.error("Failed publishing auth event eventId={}", event.getId(), ex);
            }
        }
        authEventRepository.saveAll(events);
    }


    private String resolveRoutingKey(AuthEventType eventType) {
        return switch (eventType) {
            case USER_REGISTERED -> AuthNotificationRabbitMqConstants.USER_REGISTERED_ROUTING_KEY;
            case LOGIN_SUCCESS -> AuthNotificationRabbitMqConstants.LOGIN_SUCCESS_ROUTING_KEY;
            case USER_LOGOUT -> AuthNotificationRabbitMqConstants.USER_LOGOUT_ROUTING_KEY;
            case REFRESH_TOKEN_ROTATED -> AuthNotificationRabbitMqConstants.REFRESH_TOKEN_ROTATED_ROUTING_KEY;
            case PASSWORD_CHANGED -> AuthNotificationRabbitMqConstants.PASSWORD_CHANGED_ROUTING_KEY;
            case PASSWORD_RESET_REQUESTED -> AuthNotificationRabbitMqConstants.PASSWORD_RESET_REQUESTED_ROUTING_KEY;
            case PASSWORD_RESET_SUCCESS -> AuthNotificationRabbitMqConstants.PASSWORD_RESET_SUCCESS_ROUTING_KEY;
            case EMAIL_VERIFIED -> AuthNotificationRabbitMqConstants.EMAIL_VERIFIED_ROUTING_KEY;
            case EMAIL_VERIFICATION_REQUESTED ->
                    AuthNotificationRabbitMqConstants.EMAIL_VERIFICATION_REQUESTED_ROUTING_KEY;
            case ACCOUNT_LOCKED -> AuthNotificationRabbitMqConstants.ACCOUNT_LOCKED_ROUTING_KEY;
            case SESSION_CREATED -> "auth.session.created";
            case SESSION_REVOKED -> "auth.session.revoked";
            case ALL_SESSIONS_REVOKED -> "auth.sessions.revoked";
        };
    }
}