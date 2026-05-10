package com.payflowx.settlement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "merchant_balances", uniqueConstraints =
        {@UniqueConstraint(name = "uk_merchant_balance_merchant", columnNames = "merchant_id")}
        , indexes = {@Index(name = "idx_merchant_balance_merchant", columnList = "merchant_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantBalance extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "merchant_id", nullable = false, unique = true)
    private UUID merchantId;

    @Builder.Default
    @Column(name = "pending_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "available_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "settled_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal settledBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "reserved_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal reservedBalance = BigDecimal.ZERO;

    @Version
    private Long version;
}