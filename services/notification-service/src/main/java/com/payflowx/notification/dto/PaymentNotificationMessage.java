package com.payflowx.notification.dto;

import java.util.Map;

public record PaymentNotificationMessage(

        String eventId,

        String correlationId,

        String merchantId,

        String sourceService,

        String eventType,

        Map<String, Object> payload
) {
}