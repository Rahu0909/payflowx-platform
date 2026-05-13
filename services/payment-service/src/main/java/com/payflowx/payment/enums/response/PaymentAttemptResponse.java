package com.payflowx.payment.enums.response;

import com.payflowx.payment.enums.PaymentAttemptStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentAttemptResponse(
        UUID id,
        UUID paymentId,
        Integer attemptNumber,
        PaymentAttemptStatus status,
        String gatewayTransactionId,
        String failureReason,
        LocalDateTime processedAt,
        Long processingTimeMs
) {
}