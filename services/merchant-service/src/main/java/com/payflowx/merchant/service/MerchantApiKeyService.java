package com.payflowx.merchant.service;

import com.payflowx.merchant.dto.request.CreateApiKeyRequest;
import com.payflowx.merchant.dto.request.ValidateApiKeyRequest;
import com.payflowx.merchant.dto.response.ApiKeyResponse;
import com.payflowx.merchant.dto.response.ValidateApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface MerchantApiKeyService {

    ApiKeyResponse createApiKey(UUID authUserId, CreateApiKeyRequest request);

    List<ApiKeyResponse> getApiKeys(UUID authUserId);

    ApiKeyResponse rotateApiKey(UUID authUserId, UUID apiKeyId);

    void revokeApiKey(UUID authUserId, UUID apiKeyId);

    ValidateApiKeyResponse validateSecretKey(ValidateApiKeyRequest request);
}