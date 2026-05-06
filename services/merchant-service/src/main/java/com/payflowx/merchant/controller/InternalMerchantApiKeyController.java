package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.ValidateApiKeyRequest;
import com.payflowx.merchant.dto.response.ValidateApiKeyResponse;
import com.payflowx.merchant.service.MerchantApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/merchant/api-keys")
@RequiredArgsConstructor
public class InternalMerchantApiKeyController {

    private final MerchantApiKeyService apiKeyService;

    @PostMapping("/validate")
    public ApiResponse<ValidateApiKeyResponse> validateKey(@Valid @RequestBody ValidateApiKeyRequest request) {
        return ApiResponse.success(apiKeyService.validateSecretKey(request), "API key validated successfully");
    }
}