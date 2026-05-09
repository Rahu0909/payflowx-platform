package com.payflowx.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID merchantId,
        String merchantBusinessName,
        BigDecimal amount,
        String currency,
        String receipt,
        String status,
        String customerEmail,
        String customerPhone,
        LocalDateTime expiresAt,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
}