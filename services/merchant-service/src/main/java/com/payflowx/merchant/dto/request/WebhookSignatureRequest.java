package com.payflowx.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record WebhookSignatureRequest(
        UUID webhookId,
        @NotBlank
        String payload
) {
}