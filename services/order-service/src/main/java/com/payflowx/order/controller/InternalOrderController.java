package com.payflowx.order.controller;

import com.payflowx.order.dto.ApiResponse;
import com.payflowx.order.dto.response.InternalOrderValidationResponse;
import com.payflowx.order.service.InternalOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {
    private final InternalOrderService internalOrderService;

    @GetMapping("/{orderId}/validate")
    public ApiResponse<InternalOrderValidationResponse> validateOrder(@PathVariable UUID orderId) {
        return ApiResponse.success(internalOrderService.validateOrder(orderId), "Order validated successfully");
    }

    @PatchMapping("/{orderId}/mark-paid")
    public ApiResponse<Void> markOrderPaid(@PathVariable UUID orderId) {
        internalOrderService.markOrderPaid(orderId);
        return ApiResponse.success(null, "Order marked as paid");
    }
}