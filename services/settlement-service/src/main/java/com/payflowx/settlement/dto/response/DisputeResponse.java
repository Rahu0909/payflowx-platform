package com.payflowx.settlement.dto.response;

import com.payflowx.settlement.enums.DisputeReason;
import com.payflowx.settlement.enums.DisputeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DisputeResponse(

        UUID id,

        UUID paymentId,

        UUID merchantId,

        BigDecimal amount,

        DisputeReason reason,

        DisputeStatus status,

        String description,

        LocalDateTime resolvedAt,

        LocalDateTime createdAt) {
}