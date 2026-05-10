package com.payflowx.payment.dto.request;

import com.payflowx.payment.enums.Currency;
import com.payflowx.payment.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(

        @NotNull
        UUID orderId,

        @NotNull
        @DecimalMin(
                value = "1.00",
                inclusive = true
        )
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount,

        @NotNull
        Currency currency,

        @NotNull
        PaymentMethod paymentMethod,

        @Size(max = 500)
        String description
) {
}