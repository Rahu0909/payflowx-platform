package com.payflowx.settlement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePayoutRequest(

        @NotNull @DecimalMin(value = "1.00") BigDecimal amount,

        @NotNull String currency) {
}