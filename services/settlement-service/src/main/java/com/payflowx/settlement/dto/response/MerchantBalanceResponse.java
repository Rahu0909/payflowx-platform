package com.payflowx.settlement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MerchantBalanceResponse(

        UUID id,

        UUID merchantId,

        BigDecimal pendingBalance,

        BigDecimal availableBalance,

        BigDecimal settledBalance,

        BigDecimal reservedBalance,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {
}