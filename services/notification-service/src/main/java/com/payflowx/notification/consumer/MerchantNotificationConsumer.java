package com.payflowx.notification.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payflowx.notification.config.RabbitMqConfig;
import com.payflowx.notification.dto.MerchantNotificationMessage;
import com.payflowx.notification.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantNotificationConsumer {

    private final NotificationEventService notificationEventService;

    @RabbitListener(queues = RabbitMqConfig.MERCHANT_KYC_APPROVED_QUEUE)
    public void consumeMerchantKycApprovedEvent(Map<String, Object> payload,
                                                @Header(name = "X-PAYFLOWX-EVENT-ID") String eventId,
                                                @Header(name = "X-PAYFLOWX-CORRELATION-ID") String correlationId,
                                                @Header(name = "X-PAYFLOWX-MERCHANT-ID") String merchantId,
                                                @Header(name = "X-PAYFLOWX-EVENT-TYPE") String eventType,
                                                @Header(name = "X-PAYFLOWX-SOURCE-SERVICE", required = false) String sourceService) throws JsonProcessingException {
        try {
            MDC.put("correlationId", correlationId);
            log.info("Merchant approved notification received eventId={} merchantId={}", eventId, merchantId);
            MerchantNotificationMessage message = new MerchantNotificationMessage(eventId, correlationId, merchantId,
                    sourceService == null || sourceService.isBlank() ? "merchant-service" : sourceService,
                    eventType, payload);
            notificationEventService.processMerchantNotification(message);
        } finally {
            MDC.clear();
        }
    }

    @RabbitListener(queues = RabbitMqConfig.MERCHANT_KYC_REJECTED_QUEUE)
    public void consumeMerchantKycRejectedEvent(Map<String, Object> payload,
                                                @Header(name = "X-PAYFLOWX-EVENT-ID") String eventId,
                                                @Header(name = "X-PAYFLOWX-CORRELATION-ID") String correlationId,
                                                @Header(name = "X-PAYFLOWX-MERCHANT-ID") String merchantId,
                                                @Header(name = "X-PAYFLOWX-EVENT-TYPE") String eventType,
                                                @Header(name = "X-PAYFLOWX-SOURCE-SERVICE", required = false) String sourceService) throws JsonProcessingException {
        try {
            MDC.put("correlationId", correlationId);
            log.info("Merchant rejected notification received eventId={} merchantId={}", eventId, merchantId);
            MerchantNotificationMessage message = new MerchantNotificationMessage(eventId, correlationId, merchantId, sourceService == null || sourceService.isBlank() ? "merchant-service" : sourceService, eventType, payload);
            notificationEventService.processMerchantNotification(message);
        } finally {
            MDC.clear();
        }
    }
}