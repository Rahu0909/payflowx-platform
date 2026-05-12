package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.UpdateMerchantSettlementConfigRequest;
import com.payflowx.merchant.dto.response.MerchantSettlementConfigResponse;
import com.payflowx.merchant.service.MerchantSettlementConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/merchants")
@RequiredArgsConstructor
public class AdminMerchantSettlementConfigController {

    private final MerchantSettlementConfigService service;

    @GetMapping("/{merchantId}/settlement-config")
    public ApiResponse<MerchantSettlementConfigResponse> getConfig(@PathVariable UUID merchantId) {
        return ApiResponse.success(service.getAdminSettlementConfig(merchantId),
                "Settlement config fetched successfully");
    }

    @PatchMapping("/{merchantId}/settlement-config")
    public ApiResponse<MerchantSettlementConfigResponse> updateConfig(
            @PathVariable UUID merchantId,
            @Valid @RequestBody UpdateMerchantSettlementConfigRequest request) {
        return ApiResponse.success(service.updateSettlementConfig(merchantId, request),
                "Settlement config updated successfully");
    }
}