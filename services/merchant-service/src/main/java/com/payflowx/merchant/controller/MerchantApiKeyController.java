package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.CreateApiKeyRequest;
import com.payflowx.merchant.dto.response.ApiKeyResponse;
import com.payflowx.merchant.service.MerchantApiKeyService;
import com.payflowx.merchant.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchants/api-keys")
@RequiredArgsConstructor
public class MerchantApiKeyController {

    private final MerchantApiKeyService apiKeyService;

    @PostMapping
    public ApiResponse<ApiKeyResponse> createKey(@Valid @RequestBody CreateApiKeyRequest request) {
        return ApiResponse.success(apiKeyService.createApiKey(SecurityUtil.getCurrentUserId(), request), "API key created successfully");
    }

    @GetMapping
    public ApiResponse<List<ApiKeyResponse>> getKeys() {
        return ApiResponse.success(apiKeyService.getApiKeys(SecurityUtil.getCurrentUserId()), "API keys fetched successfully");
    }

    @PostMapping("/{apiKeyId}/rotate")
    public ApiResponse<ApiKeyResponse> rotateKey(@PathVariable UUID apiKeyId) {
        return ApiResponse.success(apiKeyService.rotateApiKey(SecurityUtil.getCurrentUserId(), apiKeyId), "API key rotated successfully");
    }

    @DeleteMapping("/{apiKeyId}")
    public ApiResponse<Void> revokeKey(@PathVariable UUID apiKeyId) {
        apiKeyService.revokeApiKey(SecurityUtil.getCurrentUserId(), apiKeyId);
        return ApiResponse.success(null, "API key revoked successfully");
    }
}