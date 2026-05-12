package com.payflowx.payment.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record InternalPaymentSettlementResponse(
        UUID paymentId,
        UUID merchantId,
        BigDecimal netSettlementAmount,
        String currency,
        String paymentStatus,
        Integer settlementDelayDays) {
}