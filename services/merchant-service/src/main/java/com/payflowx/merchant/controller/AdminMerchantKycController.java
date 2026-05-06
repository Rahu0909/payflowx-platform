package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.RejectMerchantKycRequest;
import com.payflowx.merchant.dto.response.MerchantKycResponse;
import com.payflowx.merchant.service.MerchantKycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/merchants")
@RequiredArgsConstructor
public class AdminMerchantKycController {

    private final MerchantKycService merchantKycService;
    @PatchMapping("/{merchantId}/kyc/approve")
    public ApiResponse<MerchantKycResponse> approveKyc(
            @PathVariable UUID merchantId
    ) {
        return ApiResponse.success(
                merchantKycService.approveKyc(merchantId),
                "Merchant KYC approved successfully"
        );
    }
    @PatchMapping("/{merchantId}/kyc/reject")
    public ApiResponse<MerchantKycResponse> rejectKyc(
            @PathVariable UUID merchantId,
            @Valid @RequestBody RejectMerchantKycRequest request
    ) {
        return ApiResponse.success(
                merchantKycService.rejectKyc(merchantId, request),
                "Merchant KYC rejected successfully"
        );
    }
}