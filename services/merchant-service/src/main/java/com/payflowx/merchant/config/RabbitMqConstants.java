package com.payflowx.merchant.config;

public final class RabbitMqConstants {

    private RabbitMqConstants() {
    }

    public static final String NOTIFICATION_EXCHANGE =
            "payflowx.notification.exchange";

    public static final String MERCHANT_KYC_APPROVED_ROUTING_KEY =
            "merchant.kyc.approved";

    public static final String MERCHANT_KYC_REJECTED_ROUTING_KEY =
            "merchant.kyc.rejected";
}