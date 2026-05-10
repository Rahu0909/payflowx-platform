package com.payflowx.payment.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record InternalOrderValidationResponse(

        UUID orderId,

        UUID merchantId,

        BigDecimal amount,

        String currency,

        String status,

        boolean valid) {
}