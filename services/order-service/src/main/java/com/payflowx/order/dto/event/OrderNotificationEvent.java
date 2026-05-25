package com.payflowx.order.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationEvent {

    private UUID eventId;

    private UUID orderId;

    private UUID merchantId;

    private String merchantBusinessName;

    private String customerEmail;

    private String customerPhone;

    private String eventType;

    private String message;

    private BigDecimal amount;

    private String currency;

    private LocalDateTime occurredAt;
}