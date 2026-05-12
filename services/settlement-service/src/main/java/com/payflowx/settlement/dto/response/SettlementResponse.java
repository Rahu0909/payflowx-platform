package com.payflowx.settlement.dto.response;

import com.payflowx.settlement.enums.SettlementStatus;
import com.payflowx.settlement.enums.SettlementType;

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