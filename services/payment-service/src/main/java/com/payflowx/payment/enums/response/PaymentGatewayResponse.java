package com.payflowx.payment.enums.response;

public record PaymentGatewayResponse(
        boolean success,
        String gatewayReference,
        String transactionId,
        String failureReason
) {
}