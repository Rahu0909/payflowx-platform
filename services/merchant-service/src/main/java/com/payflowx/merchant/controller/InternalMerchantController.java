package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.response.InternalMerchantValidationResponse;
import com.payflowx.merchant.dto.response.MerchantWebhookResponse;
import com.payflowx.merchant.service.MerchantService;
import com.payflowx.merchant.service.MerchantValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/merchants")
@RequiredArgsConstructor
public class InternalMerchantController {

    private final MerchantValidationService validationService;
    private final MerchantService merchantService;

    @GetMapping("/{userId}/validate")
    public ApiResponse<InternalMerchantValidationResponse> validateMerchant(@PathVariable UUID userId) {
        return ApiResponse.success(validationService.validateMerchant(userId), "Merchant validated successfully");
    }

    @GetMapping("/{merchantId}/webhook")
    public ResponseEntity<MerchantWebhookResponse> getMerchantWebhook(@PathVariable String merchantId) {
        return ResponseEntity.ok(merchantService.getMerchantWebhook(merchantId));
    }
}