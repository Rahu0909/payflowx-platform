package com.payflowx.notification.dto;

import java.util.Map;
import java.util.UUID;

public record AuthNotificationMessage(
        UUID eventId,
        String eventType,
        String sourceService,
        Map<String, Object> payload
) {
}