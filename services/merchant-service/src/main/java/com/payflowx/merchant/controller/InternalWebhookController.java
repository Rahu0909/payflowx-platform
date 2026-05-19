package com.payflowx.merchant.controller;

import com.payflowx.merchant.dto.ApiResponse;
import com.payflowx.merchant.dto.request.WebhookSignatureRequest;
import com.payflowx.merchant.dto.response.InternalMerchantWebhookResponse;
import com.payflowx.merchant.dto.response.WebhookSignatureResponse;
import com.payflowx.merchant.service.MerchantWebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/webhooks")
@RequiredArgsConstructor
public class InternalWebhookController {

    private final MerchantWebhookService webhookService;

    @PostMapping("/signature")
    public ApiResponse<WebhookSignatureResponse> signPayload(@Valid @RequestBody WebhookSignatureRequest request) {
        return ApiResponse.success(webhookService.generateSignature(request), "Webhook signature generated successfully");
    }

    @GetMapping("/{merchantId}/webhook")
    public InternalMerchantWebhookResponse getMerchantWebhook(@PathVariable String merchantId) {
        return webhookService.getMerchantWebhook(merchantId);
    }
}