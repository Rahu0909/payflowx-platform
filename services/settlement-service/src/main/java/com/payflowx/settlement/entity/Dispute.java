package com.payflowx.settlement.entity;

import com.payflowx.settlement.enums.DisputeReason;
import com.payflowx.settlement.enums.DisputeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "disputes", indexes = {
        @Index(name = "idx_dispute_payment", columnList = "payment_id"),
        @Index(name = "idx_dispute_merchant", columnList = "merchant_id"),
        @Index(name = "idx_dispute_status", columnList = "status")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private DisputeReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DisputeStatus status;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}