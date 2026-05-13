package com.payflowx.settlement.service;

import com.payflowx.settlement.entity.Payout;
import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.SettlementWebhookEventType;

public interface SettlementWebhookEventService {

    void publishSettlementEvent(Settlement settlement, SettlementWebhookEventType eventType);

    void publishPayoutEvent(Payout payout, SettlementWebhookEventType eventType);
}