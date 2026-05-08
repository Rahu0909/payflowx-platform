package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.CreateWebhookRequest;
import com.payflowx.merchant.dto.request.UpdateWebhookRequest;
import com.payflowx.merchant.dto.response.WebhookResponse;
import com.payflowx.merchant.service.MerchantWebhookService;
import com.payflowx.merchant.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchants/webhooks")
@RequiredArgsConstructor
public class MerchantWebhookController {

    private final MerchantWebhookService webhookService;

    @PostMapping
    public ApiResponse<WebhookResponse> createWebhook(@Valid @RequestBody CreateWebhookRequest request) {
        return ApiResponse.success(webhookService.createWebhook(SecurityUtil.getCurrentUserId(), request), "Webhook created successfully");
    }

    @GetMapping
    public ApiResponse<List<WebhookResponse>> getWebhooks() {
        return ApiResponse.success(webhookService.getWebhooks(SecurityUtil.getCurrentUserId()), "Webhooks fetched successfully");
    }

    @PatchMapping("/{webhookId}")
    public ApiResponse<WebhookResponse> updateWebhook(@PathVariable UUID webhookId, @Valid @RequestBody UpdateWebhookRequest request) {
        return ApiResponse.success(webhookService.updateWebhook(SecurityUtil.getCurrentUserId(), webhookId, request), "Webhook updated successfully");
    }

    @DeleteMapping("/{webhookId}")
    public ApiResponse<Void> deleteWebhook(@PathVariable UUID webhookId) {
        webhookService.deleteWebhook(SecurityUtil.getCurrentUserId(), webhookId);
        return ApiResponse.success(null, "Webhook deleted successfully");
    }
}