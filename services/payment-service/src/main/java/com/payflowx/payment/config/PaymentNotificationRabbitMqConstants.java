package com.payflowx.payment.config;

public final class PaymentNotificationRabbitMqConstants {

    private PaymentNotificationRabbitMqConstants() {
    }

    public static final String NOTIFICATION_EXCHANGE = "payflowx.notification.exchange";

    public static final String PAYMENT_CREATED_ROUTING_KEY = "payment.created";
    public static final String PAYMENT_PROCESSING_ROUTING_KEY = "payment.processing";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    public static final String REFUND_CREATED_ROUTING_KEY = "refund.created";
    public static final String REFUND_SUCCESS_ROUTING_KEY = "refund.success";
    public static final String REFUND_FAILED_ROUTING_KEY = "refund.failed";

    public static final String SOURCE_SERVICE = "payment-service";
}