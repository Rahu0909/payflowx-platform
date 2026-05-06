package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.SubmitMerchantKycRequest;
import com.payflowx.merchant.dto.response.MerchantKycResponse;
import com.payflowx.merchant.service.MerchantKycService;
import com.payflowx.merchant.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchants/kyc")
@RequiredArgsConstructor
public class MerchantKycController {
    private final MerchantKycService merchantKycService;

    @PostMapping
    public ApiResponse<MerchantKycResponse> submitKyc(@Valid @RequestBody SubmitMerchantKycRequest request) {
        return ApiResponse.success(merchantKycService.submitKyc(SecurityUtil.getCurrentUserId(), request), "Merchant KYC submitted successfully");
    }

    @GetMapping
    public ApiResponse<MerchantKycResponse> getKyc() {
        return ApiResponse.success(merchantKycService.getKyc(SecurityUtil.getCurrentUserId()), "Merchant KYC fetched successfully");
    }
}