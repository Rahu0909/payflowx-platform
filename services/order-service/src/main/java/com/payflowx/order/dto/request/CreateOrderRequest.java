package com.payflowx.order.dto.request;

import com.payflowx.order.enums.Currency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateOrderRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00",
                message = "Minimum amount is 1")
        BigDecimal amount,

        @NotNull(message = "Currency is required")
        Currency currency,

        @NotBlank(message = "Receipt is required")
        @Size(max = 100)
        String receipt,

        @Email(message = "Invalid customer email")
        String customerEmail,

        @Size(max = 20)
        String customerPhone
) {
}