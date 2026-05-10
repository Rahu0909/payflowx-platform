package com.payflowx.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RefundRequest(

        @NotNull
        @DecimalMin(
                value = "1.00",
                inclusive = true
        )
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount,

        @Size(max = 500)
        String reason
) {
}