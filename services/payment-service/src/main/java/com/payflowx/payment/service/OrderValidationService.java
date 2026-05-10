package com.payflowx.payment.service;

import com.payflowx.payment.dto.response.InternalOrderValidationResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderValidationService {

    InternalOrderValidationResponse validateOrder(UUID orderId, BigDecimal amount, String currency);

    void markOrderPaid(UUID orderId);
}