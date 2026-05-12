package com.payflowx.payment.dto.response;

import java.math.BigDecimal;

public record PaymentFinancialDetails(

        BigDecimal grossAmount,

        BigDecimal platformFeeAmount,

        BigDecimal reserveAmount,

        BigDecimal netSettlementAmount,

        Integer settlementDelayDays) {
}