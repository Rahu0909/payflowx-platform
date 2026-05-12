package com.payflowx.payment.client;

import com.payflowx.payment.dto.ApiResponse;
import com.payflowx.payment.dto.response.InternalMerchantSettlementConfigResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "merchant-service")
public interface MerchantClient {

    @GetMapping("/internal/merchant-settlement-configs/{merchantId}")
    ApiResponse<InternalMerchantSettlementConfigResponse> getSettlementConfig(
            @PathVariable UUID merchantId);
}