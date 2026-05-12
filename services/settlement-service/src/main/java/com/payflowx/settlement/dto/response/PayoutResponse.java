package com.payflowx.settlement.dto.response;

import com.payflowx.settlement.enums.PayoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PayoutResponse(
        UUID id,
        String payoutReference,
        UUID merchantId,
        BigDecimal amount,
        String currency,
        PayoutStatus status,
        String bankReference,
        String failureReason,
        LocalDateTime processedAt,
        LocalDateTime createdAt) {
}