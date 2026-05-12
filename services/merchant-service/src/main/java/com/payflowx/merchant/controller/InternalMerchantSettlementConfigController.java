package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.response.InternalMerchantSettlementConfigResponse;
import com.payflowx.merchant.service.MerchantSettlementConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/merchant-settlement-configs")
@RequiredArgsConstructor
public class InternalMerchantSettlementConfigController {

    private final MerchantSettlementConfigService service;

    @GetMapping("/{merchantId}")
    public ApiResponse<InternalMerchantSettlementConfigResponse> getConfig(@PathVariable UUID merchantId) {
        return ApiResponse.success(service.getSettlementConfig(merchantId), "Settlement config fetched successfully");
    }
}