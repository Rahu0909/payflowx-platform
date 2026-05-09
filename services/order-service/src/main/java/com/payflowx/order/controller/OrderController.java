package com.payflowx.order.controller;

import com.payflowx.order.dto.ApiResponse;
import com.payflowx.order.dto.request.CreateOrderRequest;
import com.payflowx.order.dto.response.OrderResponse;
import com.payflowx.order.service.IdempotencyService;
import com.payflowx.order.service.OrderService;
import com.payflowx.order.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(orderService.createOrder(userId, request, idempotencyKey), "Order created successfully");
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable UUID orderId) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(orderService.getOrder(userId, orderId), "Order fetched successfully");
    }

    @GetMapping
    public ApiResponse<Page<OrderResponse>> getOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(orderService.getMyOrders(userId, PageRequest.of(page, size)), "Orders fetched successfully");
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> cancelOrder(@PathVariable UUID orderId) {
        UUID userId = SecurityUtil.getCurrentUserId();
        orderService.cancelOrder(userId, orderId);
        return ApiResponse.success(null, "Order cancelled successfully");
    }
}