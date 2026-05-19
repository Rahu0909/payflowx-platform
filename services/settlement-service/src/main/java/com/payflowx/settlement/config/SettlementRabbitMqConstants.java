package com.payflowx.settlement.config;

public final class SettlementRabbitMqConstants {

    private SettlementRabbitMqConstants() {
    }

    public static final String NOTIFICATION_EXCHANGE = "payflowx.notification.exchange";

    /*
     * Settlement Events
     */

    public static final String SETTLEMENT_CREATED_ROUTING_KEY = "settlement.created";

    public static final String SETTLEMENT_COMPLETED_ROUTING_KEY = "settlement.completed";

    public static final String SETTLEMENT_FAILED_ROUTING_KEY = "settlement.failed";

    /*
     * Payout Events
     */

    public static final String PAYOUT_CREATED_ROUTING_KEY = "payout.created";

    public static final String PAYOUT_SUCCESS_ROUTING_KEY = "payout.success";

    public static final String PAYOUT_FAILED_ROUTING_KEY = "payout.failed";

    public static final String PAYOUT_REVERSED_ROUTING_KEY = "payout.reversed";
}