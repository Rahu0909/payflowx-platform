package com.payflowx.settlement.entity;

import com.payflowx.settlement.enums.SettlementStatus;
import com.payflowx.settlement.enums.SettlementType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlements", indexes = {@Index(name = "idx_settlement_merchant", columnList = "merchant_id")
        , @Index(name = "idx_settlement_status", columnList = "status")
        , @Index(name = "idx_settlement_payment", columnList = "payment_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "settlement_reference", nullable = false, unique = true, length = 100)
    private String settlementReference;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "refund_id")
    private UUID refundId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private SettlementType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SettlementStatus status;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "description", length = 500)
    private String description;

    @Version
    private Long version;

    @Column(name = "release_at")
    private LocalDateTime releaseAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}