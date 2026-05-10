package com.payflowx.payment.dto.response;

import com.payflowx.payment.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RefundResponse(

        UUID id, String refundReference, UUID paymentId,

        BigDecimal amount,

        String reason,

        RefundStatus status,

        LocalDateTime createdAt) {
}