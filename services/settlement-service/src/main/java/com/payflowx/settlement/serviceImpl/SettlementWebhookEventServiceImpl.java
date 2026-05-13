package com.payflowx.settlement.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.settlement.entity.Payout;
import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.entity.SettlementWebhookEvent;
import com.payflowx.settlement.enums.SettlementWebhookEventType;
import com.payflowx.settlement.repository.SettlementWebhookEventRepository;
import com.payflowx.settlement.service.SettlementWebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementWebhookEventServiceImpl implements SettlementWebhookEventService {
    private final SettlementWebhookEventRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishSettlementEvent(Settlement settlement, SettlementWebhookEventType eventType) {
        try {

            Map<String, Object> payload = Map.of("settlementId", settlement.getId(),
                    "settlementReference", settlement.getSettlementReference(),
                    "merchantId", settlement.getMerchantId(),
                    "paymentId", settlement.getPaymentId(),
                    "amount", settlement.getAmount(),
                    "currency", settlement.getCurrency(),
                    "status", settlement.getStatus(),
                    "eventType", eventType.name());
            SettlementWebhookEvent event = SettlementWebhookEvent.builder().merchantId(settlement.getMerchantId())
                    .eventType(eventType).payload(objectMapper.writeValueAsString(payload)).build();
            repository.save(event);
            log.info("Settlement webhook event published settlementId={} type={}", settlement.getId(), eventType);
        } catch (Exception ex) {
            log.error("Settlement webhook publish failed settlementId={}", settlement.getId(), ex);
        }
    }

    @Override
    public void publishPayoutEvent(Payout payout, SettlementWebhookEventType eventType) {
        try {
            Map<String, Object> payload = Map.of("payoutId", payout.getId(),
                    "payoutReference", payout.getPayoutReference(),
                    "merchantId", payout.getMerchantId(),
                    "amount", payout.getAmount(),
                    "currency", payout.getCurrency(),
                    "status", payout.getStatus(),
                    "bankReference", payout.getBankReference(),
                    "eventType", eventType.name());
            SettlementWebhookEvent event = SettlementWebhookEvent.builder().merchantId(payout.getMerchantId())
                    .eventType(eventType).payload(objectMapper.writeValueAsString(payload)).build();
            repository.save(event);
            log.info("Payout webhook event published payoutId={} type={}", payout.getId(), eventType);
        } catch (Exception ex) {
            log.error("Payout webhook publish failed payoutId={}", payout.getId(), ex);
        }
    }
}