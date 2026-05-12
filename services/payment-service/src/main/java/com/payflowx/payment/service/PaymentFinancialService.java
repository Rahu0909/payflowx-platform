package com.payflowx.payment.service;

import com.payflowx.payment.dto.response.PaymentFinancialDetails;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentFinancialService {

    PaymentFinancialDetails calculateFinancials(UUID merchantId, BigDecimal grossAmount);
}