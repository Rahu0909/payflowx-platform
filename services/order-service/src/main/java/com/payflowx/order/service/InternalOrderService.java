package com.payflowx.order.service;

import com.payflowx.order.dto.response.InternalOrderValidationResponse;

import java.util.UUID;

public interface InternalOrderService {
    InternalOrderValidationResponse validateOrder(UUID orderId);
    void markOrderPaid(UUID orderId);
}