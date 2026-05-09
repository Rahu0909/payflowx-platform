package com.payflowx.order.client;

import com.payflowx.order.dto.ApiResponse;
import com.payflowx.order.dto.response.InternalMerchantValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "merchant-service")
public interface MerchantClient {

    @GetMapping(
            "/internal/merchants/{userId}/validate"
    )
    ApiResponse<InternalMerchantValidationResponse>
    validateMerchant(
            @PathVariable UUID userId
    );
}