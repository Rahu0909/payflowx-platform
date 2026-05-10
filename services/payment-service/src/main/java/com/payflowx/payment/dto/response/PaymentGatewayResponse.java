package com.payflowx.payment.dto.response;

public record PaymentGatewayResponse(
        boolean success,
        String gatewayReference,
        String transactionId,
        String failureReason
) {
}