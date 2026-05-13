package com.payflowx.payment.enums.response;

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