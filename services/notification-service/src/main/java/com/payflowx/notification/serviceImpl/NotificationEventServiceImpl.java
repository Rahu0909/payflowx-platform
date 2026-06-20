package com.payflowx.notification.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.notification.config.AuditRabbitMqConstants;
import com.payflowx.notification.dto.AuditEventMessage;
import com.payflowx.notification.dto.MerchantNotificationMessage;
import com.payflowx.notification.dto.PaymentNotificationMessage;
import com.payflowx.notification.dto.TreasuryNotificationMessage;
import com.payflowx.notification.entity.NotificationEvent;
import com.payflowx.notification.enums.ErrorCode;
import com.payflowx.notification.enums.NotificationChannel;
import com.payflowx.notification.enums.NotificationEventType;
import com.payflowx.notification.enums.NotificationStatus;
import com.payflowx.notification.exception.BusinessValidationException;
import com.payflowx.notification.repository.NotificationEventRepository;
import com.payflowx.notification.service.NotificationEventService;
import com.payflowx.notification.service.WebhookDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventServiceImpl implements NotificationEventService {
    private final NotificationEventRepository notificationEventRepository;
    private final WebhookDispatcherService webhookDispatcherService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void processPaymentNotification(PaymentNotificationMessage message) throws JsonProcessingException {
        boolean alreadyProcessed = notificationEventRepository.findByEventId(message.eventId()).isPresent();
        if (alreadyProcessed) {
            log.warn("Duplicate payment notification ignored eventId={}", message.eventId());
            return;
        }
        NotificationEventType eventType;
        try {
            eventType = NotificationEventType.valueOf(message.eventType());
        } catch (IllegalArgumentException ex) {
            throw new BusinessValidationException(ErrorCode.INVALID_REQUEST, "Unsupported payment event type: " + message.eventType());
        }
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setEventId(message.eventId());
        notificationEvent.setCorrelationId(message.correlationId());
        notificationEvent.setAggregateId(message.merchantId());
        notificationEvent.setSourceService(message.sourceService());
        notificationEvent.setEventType(eventType);
        notificationEvent.setChannel(NotificationChannel.WEBHOOK);
        notificationEvent.setStatus(NotificationStatus.PENDING);
        notificationEvent.setPayload(objectMapper.writeValueAsString(message.payload()));
        notificationEventRepository.save(notificationEvent);
        publishAuditEvent(
                notificationEvent.getEventId(),
                notificationEvent.getCorrelationId(),
                notificationEvent.getAggregateId(),
                "NOTIFICATION_RECEIVED",
                message
        );
        log.info("Payment notification persisted eventId={} merchantId={} eventType={}", message.eventId(), message.merchantId(), eventType);
        if (shouldDispatchWebhook(eventType)) {
            try {
                webhookDispatcherService.dispatchWebhook(notificationEvent);
            } catch (Exception ex) {
                log.error("Webhook dispatch failed eventId={}", notificationEvent.getEventId(), ex);
            }
        }
    }

    private boolean shouldDispatchWebhook(NotificationEventType eventType) {
        return eventType == NotificationEventType.PAYMENT_SUCCESS || eventType == NotificationEventType.PAYMENT_FAILED || eventType == NotificationEventType.REFUND_SUCCESS || eventType == NotificationEventType.REFUND_FAILED;
    }

    @Override
    @Transactional
    public void processTreasuryNotification(TreasuryNotificationMessage message) throws JsonProcessingException {
        boolean alreadyProcessed = notificationEventRepository.findByEventId(message.eventId()).isPresent();
        if (alreadyProcessed) {
            log.warn("Duplicate treasury notification ignored eventId={}", message.eventId());
            return;
        }
        NotificationEventType eventType = NotificationEventType.valueOf(message.eventType());
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setEventId(message.eventId());
        notificationEvent.setCorrelationId(message.correlationId());
        notificationEvent.setAggregateId(message.merchantId());
        notificationEvent.setSourceService(message.sourceService());
        notificationEvent.setEventType(eventType);
        notificationEvent.setChannel(NotificationChannel.WEBHOOK);
        notificationEvent.setStatus(NotificationStatus.PENDING);
        notificationEvent.setPayload(objectMapper.writeValueAsString(message.payload()));
        notificationEventRepository.save(notificationEvent);
        publishAuditEvent(
                notificationEvent.getEventId(),
                notificationEvent.getCorrelationId(),
                notificationEvent.getAggregateId(),
                "NOTIFICATION_RECEIVED",
                message
        );
        log.info("Treasury notification persisted eventId={} merchantId={} eventType={}", message.eventId(), message.merchantId(), eventType);
        if (shouldDispatchTreasuryWebhook(eventType)) {
            try {
                webhookDispatcherService.dispatchWebhook(notificationEvent);
            } catch (Exception ex) {
                log.error("Treasury webhook dispatch failed eventId={}", notificationEvent.getEventId(), ex);
            }
        }
    }

    private boolean shouldDispatchTreasuryWebhook(NotificationEventType eventType) {
        return eventType == NotificationEventType.SETTLEMENT_COMPLETED
                || eventType == NotificationEventType.PAYOUT_SUCCESS ||
                eventType == NotificationEventType.PAYOUT_FAILED;
    }

    @Override
    @Transactional
    public void processMerchantNotification(MerchantNotificationMessage message) throws JsonProcessingException {
        boolean alreadyProcessed = notificationEventRepository.findByEventId(message.eventId()).isPresent();
        if (alreadyProcessed) {
            log.warn("Duplicate merchant notification ignored eventId={}", message.eventId());
            return;
        }
        NotificationEventType eventType = NotificationEventType.valueOf(message.eventType());
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setEventId(message.eventId());
        notificationEvent.setCorrelationId(message.correlationId());
        notificationEvent.setAggregateId(message.merchantId());
        notificationEvent.setSourceService(message.sourceService());
        notificationEvent.setEventType(eventType);
        notificationEvent.setChannel(NotificationChannel.WEBHOOK);
        notificationEvent.setStatus(NotificationStatus.PENDING);
        notificationEvent.setPayload(objectMapper.writeValueAsString(message.payload()));
        notificationEventRepository.save(notificationEvent);
        publishAuditEvent(
                notificationEvent.getEventId(),
                notificationEvent.getCorrelationId(),
                notificationEvent.getAggregateId(),
                "NOTIFICATION_RECEIVED",
                message
        );
        log.info("Merchant notification persisted eventId={} merchantId={} eventType={}", message.eventId(), message.merchantId(), eventType);
        try {
            webhookDispatcherService.dispatchWebhook(notificationEvent);
        } catch (Exception ex) {
            log.error("Merchant webhook dispatch failed eventId={}", notificationEvent.getEventId(), ex);

        }
    }

    private void publishAuditEvent(String eventId, String correlationId, String aggregateId, String eventType, Object payload) {
        AuditEventMessage auditEvent = new AuditEventMessage(eventId, correlationId,
                aggregateId, "notification-service", eventType, payload);
        rabbitTemplate.convertAndSend(AuditRabbitMqConstants.AUDIT_EXCHANGE,
                AuditRabbitMqConstants.NOTIFICATION_AUDIT_ROUTING_KEY, auditEvent);
    }
}