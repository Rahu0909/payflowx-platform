package com.payflowx.merchant.entity;

import com.payflowx.merchant.enums.WebhookEventType;
import com.payflowx.merchant.enums.WebhookStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "merchant_webhooks",
        indexes = {
                @Index(name = "idx_webhook_merchant",
                        columnList = "merchant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantWebhook extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "webhook_url",
            nullable = false,
            length = 500)
    private String webhookUrl;

    @Column(name = "webhook_secret",
            nullable = false,
            length = 255)
    private String webhookSecret;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WebhookStatus status = WebhookStatus.ACTIVE;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "merchant_webhook_events",
            joinColumns = @JoinColumn(name = "webhook_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private Set<WebhookEventType> subscribedEvents =
            new HashSet<>();

    @Column(name = "last_triggered_at")
    private LocalDateTime lastTriggeredAt;

    @Builder.Default
    @Column(name = "failure_count")
    private Integer failureCount = 0;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}