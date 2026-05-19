package com.payflowx.notification.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventServiceImpl implements NotificationEventService {
    private final NotificationEventRepository notificationEventRepository;
    private final WebhookDispatcherService webhookDispatcherService;
    private final ObjectMapper objectMapper;

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
        log.info("Payment notification persisted eventId={} merchantId={} eventType={}", message.eventId(), message.merchantId(), eventType);
        if (shouldDispatchWebhook(eventType)) {
            webhookDispatcherService.dispatchWebhook(notificationEvent);
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
        log.info("Treasury notification persisted eventId={} merchantId={} eventType={}", message.eventId(), message.merchantId(), eventType);
        if (shouldDispatchTreasuryWebhook(eventType)) {
            webhookDispatcherService.dispatchWebhook(notificationEvent);
        }
    }

    private boolean shouldDispatchTreasuryWebhook(NotificationEventType eventType) {
        return eventType == NotificationEventType.SETTLEMENT_COMPLETED || eventType == NotificationEventType.PAYOUT_SUCCESS || eventType == NotificationEventType.PAYOUT_FAILED;
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
        log.info("Merchant notification persisted eventId={} merchantId={} eventType={}", message.eventId(), message.merchantId(), eventType);
        webhookDispatcherService.dispatchWebhook(notificationEvent);
    }
}