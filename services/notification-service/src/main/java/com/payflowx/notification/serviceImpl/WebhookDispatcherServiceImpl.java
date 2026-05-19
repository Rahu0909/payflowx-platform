package com.payflowx.notification.serviceImpl;

import com.payflowx.notification.client.MerchantServiceClient;
import com.payflowx.notification.constant.WebhookRetryConstants;
import com.payflowx.notification.dto.MerchantWebhookResponse;
import com.payflowx.notification.entity.NotificationEvent;
import com.payflowx.notification.entity.WebhookDelivery;
import com.payflowx.notification.enums.WebhookDeliveryStatus;
import com.payflowx.notification.exception.NonRetryableNotificationException;
import com.payflowx.notification.exception.RetryableNotificationException;
import com.payflowx.notification.repository.WebhookDeliveryRepository;
import com.payflowx.notification.service.WebhookDispatcherService;
import com.payflowx.notification.util.HmacSignatureUtil;
import com.payflowx.notification.util.RetryBackoffUtil;
import com.payflowx.notification.util.WebhookPayloadBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookDispatcherServiceImpl implements WebhookDispatcherService {
    private final MerchantServiceClient merchantServiceClient;
    private final WebhookDeliveryRepository webhookDeliveryRepository;
    private final WebhookPayloadBuilder webhookPayloadBuilder;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public void dispatchWebhook(NotificationEvent notificationEvent) {
        log.info("Webhook dispatch started eventId={} correlationId={}", notificationEvent.getEventId(), notificationEvent.getCorrelationId());
        boolean alreadyDelivered = webhookDeliveryRepository.findAll().stream()
                .anyMatch(delivery -> delivery.getEventId()
                        .equals(notificationEvent.getEventId()) && delivery.getStatus() == WebhookDeliveryStatus.SUCCESS);
        if (alreadyDelivered) {
            log.warn("Webhook already delivered eventId={}", notificationEvent.getEventId());
            return;
        }
        MerchantWebhookResponse webhookResponse = merchantServiceClient.getMerchantWebhook(notificationEvent.getAggregateId());
        validateWebhookConfiguration(webhookResponse);
        String payload = webhookPayloadBuilder.buildPayload(notificationEvent);
        String signature = HmacSignatureUtil.generateSignature(payload, webhookResponse.getWebhookSecret());
        WebhookDelivery delivery = createWebhookDelivery(notificationEvent, webhookResponse, payload, signature);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-PAYFLOWX-SIGNATURE", signature);
            headers.add("X-PAYFLOWX-EVENT", notificationEvent.getEventType().name());
            headers.add("X-PAYFLOWX-EVENT-ID", notificationEvent.getEventId());
            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.exchange(webhookResponse.getWebhookUrl(), HttpMethod.POST, request, String.class);
            handleSuccessResponse(delivery, response);
        } catch (ResourceAccessException ex) {
            handleRetryableFailure(delivery, "Webhook timeout or network failure");
            throw new RetryableNotificationException(ex.getMessage());
        } catch (Exception ex) {
            handleRetryableFailure(delivery, ex.getMessage());
            throw new RetryableNotificationException(ex.getMessage());
        }
    }

    private void validateWebhookConfiguration(MerchantWebhookResponse webhookResponse) {
        if (webhookResponse == null) {
            throw new NonRetryableNotificationException("Merchant webhook configuration not found");
        }
        if (Boolean.FALSE.equals(webhookResponse.getActive())) {
            throw new NonRetryableNotificationException("Merchant webhook is disabled");
        }
    }

    private WebhookDelivery createWebhookDelivery(NotificationEvent notificationEvent, MerchantWebhookResponse webhookResponse, String payload, String signature) {
        WebhookDelivery delivery = new WebhookDelivery();
        delivery.setEventId(notificationEvent.getEventId());
        delivery.setCorrelationId(notificationEvent.getCorrelationId());
        delivery.setMerchantId(notificationEvent.getAggregateId());
        delivery.setWebhookUrl(webhookResponse.getWebhookUrl());
        delivery.setEventType(notificationEvent.getEventType().name());
        delivery.setPayload(payload);
        delivery.setSignature(signature);
        delivery.setStatus(WebhookDeliveryStatus.PROCESSING);
        return webhookDeliveryRepository.save(delivery);
    }

    private void handleSuccessResponse(WebhookDelivery delivery, ResponseEntity<String> response) {
        int statusCode = response.getStatusCode().value();
        delivery.setResponseStatusCode(statusCode);
        delivery.setResponseBody(response.getBody());
        if (statusCode >= 200 && statusCode < 300) {
            delivery.setStatus(WebhookDeliveryStatus.SUCCESS);
            delivery.setDeliveredAt(LocalDateTime.now());
            log.info("Webhook delivery successful eventId={} statusCode={}", delivery.getEventId(), statusCode);
        } else if (statusCode >= 400 && statusCode < 500) {
            delivery.setStatus(WebhookDeliveryStatus.DEAD_LETTER);
            delivery.setFailureReason("Non retryable client error");
            log.error("Webhook delivery failed permanently eventId={} statusCode={}", delivery.getEventId(), statusCode);
        } else {
            delivery.setStatus(WebhookDeliveryStatus.RETRYING);
            delivery.setRetryCount(delivery.getRetryCount() + 1);
            delivery.setNextRetryAt(LocalDateTime.now().plusMinutes(1));
            log.warn("Webhook delivery scheduled for retry eventId={} statusCode={}", delivery.getEventId(), statusCode);
        }
        webhookDeliveryRepository.save(delivery);
    }

    private void handleRetryableFailure(WebhookDelivery delivery, String reason) {
        if (delivery.getRetryCount() >= WebhookRetryConstants.MAX_RETRY_COUNT) {
            delivery.setStatus(WebhookDeliveryStatus.DEAD_LETTER);
            delivery.setFailureReason("Maximum retry attempts exceeded");
            webhookDeliveryRepository.save(delivery);
            log.error("Webhook moved to DLQ eventId={} retryCount={}", delivery.getEventId(), delivery.getRetryCount());
            return;
        }
        delivery.setStatus(WebhookDeliveryStatus.RETRYING);
        delivery.setFailureReason(reason);
        delivery.setRetryCount(delivery.getRetryCount() + 1);
        long retryDelay = RetryBackoffUtil.calculateDelayMinutes(delivery.getRetryCount());
        delivery.setNextRetryAt(LocalDateTime.now().plusMinutes(retryDelay));
        webhookDeliveryRepository.save(delivery);
        log.error("Webhook delivery retry scheduled eventId={} reason={}", delivery.getEventId(), reason);
    }
}