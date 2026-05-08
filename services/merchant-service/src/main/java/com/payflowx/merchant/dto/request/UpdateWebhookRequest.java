package com.payflowx.merchant.dto.request;

import com.payflowx.merchant.enums.WebhookEventType;
import com.payflowx.merchant.enums.WebhookStatus;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateWebhookRequest(

        @NotNull
        WebhookStatus status,

        @NotNull
        Set<WebhookEventType> subscribedEvents
) {}