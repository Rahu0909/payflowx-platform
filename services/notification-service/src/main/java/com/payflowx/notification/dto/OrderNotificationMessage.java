package com.payflowx.notification.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderNotificationMessage(

        UUID eventId,

        UUID orderId,

        UUID merchantId,

        String merchantBusinessName,

        String customerEmail,

        String customerPhone,

        String eventType,

        String message,

        BigDecimal amount,

        String currency,

        LocalDateTime occurredAt) {
}