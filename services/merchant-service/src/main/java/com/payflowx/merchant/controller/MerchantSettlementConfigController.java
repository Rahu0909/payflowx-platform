package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.response.MerchantSettlementConfigResponse;
import com.payflowx.merchant.service.MerchantSettlementConfigService;
import com.payflowx.merchant.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchants/settlement-config")
@RequiredArgsConstructor
public class MerchantSettlementConfigController {

    private final MerchantSettlementConfigService service;

    @GetMapping
    public ApiResponse<MerchantSettlementConfigResponse> getConfig() {
        return ApiResponse.success(service.getMerchantSettlementConfig(SecurityUtil.getCurrentUserId()),
                "Settlement config fetched successfully");
    }
}