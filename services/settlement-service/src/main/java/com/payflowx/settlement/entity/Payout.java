package com.payflowx.settlement.entity;

import com.payflowx.settlement.enums.PayoutStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "payouts",
        indexes = {
                @Index(name = "idx_payout_merchant", columnList = "merchant_id"),
                @Index(
                        name = "idx_payout_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_payout_reference",
                        columnList = "payout_reference"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payout extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(
            name = "payout_reference",
            nullable = false,
            unique = true,
            length = 100
    )
    private String payoutReference;

    @Column(
            name = "merchant_id",
            nullable = false
    )
    private UUID merchantId;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "currency",
            nullable = false,
            length = 10
    )
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private PayoutStatus status;

    @Column(
            name = "bank_reference",
            length = 255
    )
    private String bankReference;

    @Column(
            name = "failure_reason",
            length = 500
    )
    private String failureReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Builder.Default
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Version
    private Long version;
}