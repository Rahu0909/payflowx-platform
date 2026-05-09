package com.payflowx.order.entity;

import com.payflowx.order.enums.OrderEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_webhook_events", indexes = {@Index(name = "idx_event_order", columnList = "order_id"), @Index(name = "idx_event_status", columnList = "processed")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderWebhookEvent extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private OrderEventType eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Builder.Default
    @Column(name = "processed", nullable = false)
    private boolean processed = false;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Builder.Default
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;
}