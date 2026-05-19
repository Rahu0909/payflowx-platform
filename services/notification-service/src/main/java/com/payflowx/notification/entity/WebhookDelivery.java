package com.payflowx.notification.entity;

import com.payflowx.notification.enums.WebhookDeliveryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "webhook_deliveries", indexes = {
        @Index(name = "idx_webhook_delivery_event_id", columnList = "eventId"),
        @Index(name = "idx_webhook_delivery_status", columnList = "status"),
        @Index(name = "idx_webhook_delivery_next_retry", columnList = "nextRetryAt")})
public class WebhookDelivery extends BaseEntity {

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String webhookUrl;

    @Column(nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(columnDefinition = "TEXT")
    private String signature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookDeliveryStatus status;

    @Column
    private Integer retryCount = 0;

    @Column
    private LocalDateTime nextRetryAt;

    @Column
    private Integer responseStatusCode;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @Column
    private LocalDateTime deliveredAt;
}