package com.payflowx.merchant.serviceImpl;

import com.payflowx.merchant.config.RabbitMqConstants;
import com.payflowx.merchant.exception.RabbitMqPublisherException;
import com.payflowx.merchant.service.MerchantEventPublisher;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantEventPublisherServiceImpl implements MerchantEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishMerchantKycApproved(UUID merchantId, String businessName, String email) {
        publishEvent(merchantId, "MERCHANT_KYC_APPROVED", RabbitMqConstants.MERCHANT_KYC_APPROVED_ROUTING_KEY, Map.of("merchantId", merchantId, "businessName", businessName, "email", email, "eventType", "MERCHANT_KYC_APPROVED"));
    }

    @Override
    public void publishMerchantKycRejected(UUID merchantId, String businessName, String email, String reason) {
        publishEvent(merchantId, "MERCHANT_KYC_REJECTED", RabbitMqConstants.MERCHANT_KYC_REJECTED_ROUTING_KEY, Map.of("merchantId", merchantId, "businessName", businessName, "email", email, "reason", reason, "eventType", "MERCHANT_KYC_REJECTED"));
    }

    @Retry(name = "rabbitPublisher", fallbackMethod = "publishEventFallback")
    @CircuitBreaker(name = "rabbitPublisher", fallbackMethod = "publishEventFallback")
    private void publishEvent(UUID merchantId, String eventType, String routingKey, Map<String, Object> payload){
    UUID eventId = UUID.randomUUID();
        rabbitTemplate.convertAndSend(RabbitMqConstants.NOTIFICATION_EXCHANGE,routingKey,payload,message ->

    {
        MessageProperties props = message.getMessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setMessageId(eventId.toString());
        props.setHeader("X-PAYFLOWX-EVENT-ID", eventId.toString());
        props.setHeader("X-PAYFLOWX-CORRELATION-ID", merchantId.toString());
        props.setHeader("X-PAYFLOWX-MERCHANT-ID", merchantId.toString());
        props.setHeader("X-PAYFLOWX-EVENT-TYPE", eventType);
        props.setHeader("X-PAYFLOWX-SOURCE-SERVICE", "merchant-service");
        return message;
    });
        log.info("Merchant event published merchantId={} eventType={}",merchantId,eventType);
}

private void publishEventFallback(UUID merchantId, String eventType, String routingKey, Map<String, Object> payload, Exception ex) {
    log.error("Failed to publish merchant event merchantId={} eventType={}", merchantId, eventType, ex);
    throw new RabbitMqPublisherException("Unable to publish merchant event", ex);
}
}