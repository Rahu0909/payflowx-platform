package com.payflowx.order.service;

import com.payflowx.order.dto.request.CreateOrderRequest;
import com.payflowx.order.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID userId, CreateOrderRequest request, String idempotencyKey);

    OrderResponse getOrder(UUID userId, UUID orderId);

    Page<OrderResponse> getMyOrders(UUID userId, Pageable pageable);

    void cancelOrder(UUID userId, UUID orderId);
}