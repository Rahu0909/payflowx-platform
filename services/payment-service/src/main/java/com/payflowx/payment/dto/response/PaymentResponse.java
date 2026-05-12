package com.payflowx.payment.dto.response;

import com.payflowx.payment.enums.Currency;
import com.payflowx.payment.enums.PaymentMethod;
import com.payflowx.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(

        UUID id,
        String paymentReference,
        UUID orderId,
        UUID merchantId,
        UUID userId,

        BigDecimal amount,
        Currency currency,

        PaymentMethod paymentMethod,
        PaymentStatus status,

        String gatewayReference,
        String description,
        String failureReason,

        BigDecimal refundedAmount,

        BigDecimal grossAmount,
        BigDecimal platformFeeAmount,
        BigDecimal reserveAmount,
        BigDecimal netSettlementAmount,
        Integer settlementDelayDays,

        LocalDateTime createdAt
) {
}