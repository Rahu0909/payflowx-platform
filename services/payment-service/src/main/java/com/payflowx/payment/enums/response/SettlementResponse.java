package com.payflowx.payment.enums.response;

import com.payflowx.payment.enums.SettlementStatus;
import com.payflowx.payment.enums.SettlementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementResponse(
        UUID id,
        String settlementReference,
        UUID merchantId,
        UUID paymentId,
        SettlementType type,
        SettlementStatus status,
        BigDecimal amount,
        String currency,
        String description,
        LocalDateTime createdAt) {
}