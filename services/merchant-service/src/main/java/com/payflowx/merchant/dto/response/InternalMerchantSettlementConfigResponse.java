package com.payflowx.merchant.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record InternalMerchantSettlementConfigResponse(

        UUID merchantId,

        BigDecimal platformFeePercentage,

        Integer settlementDelayDays,

        BigDecimal rollingReservePercentage,

        BigDecimal minimumPayoutAmount,

        Boolean settlementEnabled) {
}