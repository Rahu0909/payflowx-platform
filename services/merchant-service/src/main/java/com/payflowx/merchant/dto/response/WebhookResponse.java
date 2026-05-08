package com.payflowx.merchant.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record WebhookResponse(
        UUID id,
        String webhookUrl,
        String webhookSecret,
        String status,
        Set<String> subscribedEvents,
        LocalDateTime lastTriggeredAt,
        Integer failureCount,
        LocalDateTime createdAt
) {}