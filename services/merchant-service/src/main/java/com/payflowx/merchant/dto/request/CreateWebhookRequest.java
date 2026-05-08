package com.payflowx.merchant.dto.request;

import com.payflowx.merchant.enums.WebhookEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CreateWebhookRequest(

        @NotBlank
        String webhookUrl,

        @NotEmpty
        Set<WebhookEventType> subscribedEvents
) {}