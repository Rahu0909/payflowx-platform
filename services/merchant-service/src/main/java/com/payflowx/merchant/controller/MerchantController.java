package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.CreateMerchantRequest;
import com.payflowx.merchant.dto.response.MerchantResponse;
import com.payflowx.merchant.service.MerchantService;
import com.payflowx.merchant.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping("/profile")
    public ApiResponse<MerchantResponse> createMerchant(
            @Valid @RequestBody CreateMerchantRequest request
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        return ApiResponse.success(
                merchantService.createMerchant(userId, request),
                "Merchant created successfully"
        );
    }

    @GetMapping("/profile")
    public ApiResponse<MerchantResponse> getMyMerchant() {

        UUID userId = SecurityUtil.getCurrentUserId();

        return ApiResponse.success(
                merchantService.getMerchantByUser(userId),
                "Merchant fetched successfully"
        );
    }
}