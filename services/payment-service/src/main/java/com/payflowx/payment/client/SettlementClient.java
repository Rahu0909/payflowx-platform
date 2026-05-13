package com.payflowx.payment.client;

import com.payflowx.payment.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(name = "settlement-service")
public interface SettlementClient {

    @PostMapping("/internal/settlements/payments/{paymentId}")
    ApiResponse<Void> createSettlement(
            @PathVariable UUID paymentId
    );
}