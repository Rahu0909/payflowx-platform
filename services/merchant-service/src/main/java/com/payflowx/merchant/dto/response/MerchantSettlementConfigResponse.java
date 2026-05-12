package com.payflowx.merchant.dto.response;

import java.math.BigDecimal;

public record MerchantSettlementConfigResponse(

        BigDecimal platformFeePercentage,

        Integer settlementDelayDays,

        BigDecimal rollingReservePercentage,

        BigDecimal minimumPayoutAmount,

        Boolean settlementEnabled) {
}