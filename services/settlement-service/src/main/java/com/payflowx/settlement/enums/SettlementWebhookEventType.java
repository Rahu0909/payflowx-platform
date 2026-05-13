package com.payflowx.settlement.enums;

public enum SettlementWebhookEventType {

    /*
     * Settlement Events
     */
    SETTLEMENT_CREATED,
    SETTLEMENT_COMPLETED,
    SETTLEMENT_FAILED,

    /*
     * Payout Events
     */
    PAYOUT_CREATED,
    PAYOUT_SUCCESS,
    PAYOUT_FAILED,
    PAYOUT_REVERSED
}