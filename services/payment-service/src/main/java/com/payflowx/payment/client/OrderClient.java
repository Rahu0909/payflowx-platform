package com.payflowx.payment.client;

import com.payflowx.payment.dto.ApiResponse;
import com.payflowx.payment.dto.response.InternalOrderValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/internal/orders/{orderId}/validate")
    ApiResponse<InternalOrderValidationResponse> validateOrder(
            @PathVariable UUID orderId
    );


    @PatchMapping("/internal/orders/{orderId}/mark-paid")
    ApiResponse<Void> markOrderPaid(
            @PathVariable UUID orderId
    );
}