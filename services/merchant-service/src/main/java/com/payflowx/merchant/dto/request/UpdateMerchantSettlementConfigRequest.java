package com.payflowx.merchant.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateMerchantSettlementConfigRequest(

        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 3, fraction = 2)
        BigDecimal platformFeePercentage,

        @NotNull
        @Min(0)
        @Max(30)
        Integer settlementDelayDays,

        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 3, fraction = 2)
        BigDecimal rollingReservePercentage,

        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 17, fraction = 2)
        BigDecimal minimumPayoutAmount,

        @NotNull
        Boolean settlementEnabled
) {
}