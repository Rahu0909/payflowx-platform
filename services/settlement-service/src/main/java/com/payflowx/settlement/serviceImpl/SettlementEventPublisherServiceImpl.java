package com.payflowx.settlement.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.settlement.config.SettlementRabbitMqConstants;
import com.payflowx.settlement.entity.Payout;
import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.SettlementWebhookEventType;
import com.payflowx.settlement.service.SettlementEventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementEventPublisherServiceImpl implements SettlementEventPublisherService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishSettlementEvent(Settlement settlement, SettlementWebhookEventType eventType) {
        try {
            String payload = objectMapper.writeValueAsString(settlement);
            rabbitTemplate.convertAndSend(SettlementRabbitMqConstants.NOTIFICATION_EXCHANGE, resolveRoutingKey(eventType), payload, message -> {
                MessageProperties props = message.getMessageProperties();
                props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                props.setContentEncoding("UTF-8");
                props.setMessageId(settlement.getId().toString());
                props.setHeader("X-PAYFLOWX-EVENT-ID", settlement.getId().toString());
                props.setHeader("X-PAYFLOWX-CORRELATION-ID", settlement.getSettlementReference());
                props.setHeader("X-PAYFLOWX-MERCHANT-ID", settlement.getMerchantId().toString());
                props.setHeader("X-PAYFLOWX-EVENT-TYPE", eventType.name());
                props.setHeader("X-PAYFLOWX-SOURCE-SERVICE", "settlement-service");
                return message;
            });
            log.info("Settlement event published settlementId={} eventType={}", settlement.getId(), eventType);
        } catch (JsonProcessingException ex) {
            log.error("Settlement event serialization failed settlementId={}", settlement.getId(), ex);
        }
    }

    @Override
    public void publishPayoutEvent(Payout payout, SettlementWebhookEventType eventType) {
        try {
            String payload = objectMapper.writeValueAsString(payout);
            rabbitTemplate.convertAndSend(SettlementRabbitMqConstants.NOTIFICATION_EXCHANGE, resolveRoutingKey(eventType), payload, message -> {
                MessageProperties props = message.getMessageProperties();
                props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                props.setContentEncoding("UTF-8");
                props.setMessageId(payout.getId().toString());
                props.setHeader("X-PAYFLOWX-EVENT-ID", payout.getId().toString());
                props.setHeader("X-PAYFLOWX-CORRELATION-ID", payout.getPayoutReference());
                props.setHeader("X-PAYFLOWX-MERCHANT-ID", payout.getMerchantId().toString());
                props.setHeader("X-PAYFLOWX-EVENT-TYPE", eventType.name());
                props.setHeader("X-PAYFLOWX-SOURCE-SERVICE", "settlement-service");
                return message;
            });
            log.info("Payout event published payoutId={} eventType={}", payout.getId(), eventType);
        } catch (JsonProcessingException ex) {
            log.error("Payout event serialization failed payoutId={}", payout.getId(), ex);
        }
    }

    private String resolveRoutingKey(SettlementWebhookEventType eventType) {
        return switch (eventType) {
            case SETTLEMENT_CREATED -> SettlementRabbitMqConstants.SETTLEMENT_CREATED_ROUTING_KEY;
            case SETTLEMENT_COMPLETED -> SettlementRabbitMqConstants.SETTLEMENT_COMPLETED_ROUTING_KEY;
            case SETTLEMENT_FAILED -> SettlementRabbitMqConstants.SETTLEMENT_FAILED_ROUTING_KEY;
            case PAYOUT_CREATED -> SettlementRabbitMqConstants.PAYOUT_CREATED_ROUTING_KEY;
            case PAYOUT_SUCCESS -> SettlementRabbitMqConstants.PAYOUT_SUCCESS_ROUTING_KEY;
            case PAYOUT_FAILED -> SettlementRabbitMqConstants.PAYOUT_FAILED_ROUTING_KEY;
            case PAYOUT_REVERSED -> SettlementRabbitMqConstants.PAYOUT_REVERSED_ROUTING_KEY;
        };
    }
}