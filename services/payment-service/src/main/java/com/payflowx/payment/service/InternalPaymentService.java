package com.payflowx.payment.service;

import com.payflowx.payment.dto.response.InternalPaymentSettlementResponse;

import java.util.UUID;

public interface InternalPaymentService {

    InternalPaymentSettlementResponse getSettlementPayment(UUID paymentId);
}