package com.payflowx.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserNotificationMessage(

        UUID eventId,

        UUID userId,

        String email,

        String fullName,

        String eventType,

        String message,

        LocalDateTime occurredAt) {
}