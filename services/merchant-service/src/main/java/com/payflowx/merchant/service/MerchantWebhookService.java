package com.payflowx.merchant.service;

import com.payflowx.merchant.dto.request.CreateWebhookRequest;
import com.payflowx.merchant.dto.request.UpdateWebhookRequest;
import com.payflowx.merchant.dto.request.WebhookSignatureRequest;
import com.payflowx.merchant.dto.response.WebhookResponse;
import com.payflowx.merchant.dto.response.WebhookSignatureResponse;

import java.util.List;
import java.util.UUID;

public interface MerchantWebhookService {

    WebhookResponse createWebhook(UUID authUserId, CreateWebhookRequest request);

    List<WebhookResponse> getWebhooks(UUID authUserId);

    WebhookResponse updateWebhook(UUID authUserId, UUID webhookId, UpdateWebhookRequest request);

    void deleteWebhook(UUID authUserId, UUID webhookId);

    WebhookSignatureResponse generateSignature(WebhookSignatureRequest request);
}