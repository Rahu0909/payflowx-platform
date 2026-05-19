package com.payflowx.notification.entity;

import com.payflowx.notification.enums.NotificationChannel;
import com.payflowx.notification.enums.NotificationEventType;
import com.payflowx.notification.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notification_events", indexes = {
        @Index(name = "idx_notification_status", columnList = "status"),
        @Index(name = "idx_notification_event_type", columnList = "eventType"),
        @Index(name = "idx_notification_correlation_id", columnList = "correlationId"),
        @Index(name = "idx_notification_event_id", columnList = "eventId")})
public class NotificationEvent extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String sourceService;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column
    private Integer retryCount = 0;

    @Column
    private LocalDateTime nextRetryAt;

    @Column(columnDefinition = "TEXT")
    private String failureReason;
}