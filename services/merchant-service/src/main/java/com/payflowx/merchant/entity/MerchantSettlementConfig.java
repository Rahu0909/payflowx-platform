package com.payflowx.merchant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "merchant_settlement_configs",
        uniqueConstraints = {@UniqueConstraint(name = "uk_merchant_settlement_config", columnNames = "merchant_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantSettlementConfig extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false, unique = true)
    private Merchant merchant;

    /*
     * Platform commission percentage
     * Example:
     * 2.50 = 2.5%
     */
    @Column(name = "platform_fee_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal platformFeePercentage;

    /*
     * Settlement holding period
     */
    @Column(name = "settlement_delay_days", nullable = false)
    private Integer settlementDelayDays;

    /*
     * Rolling reserve percentage
     */
    @Column(name = "rolling_reserve_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal rollingReservePercentage;

    /*
     * Minimum payout threshold
     */
    @Column(name = "minimum_payout_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal minimumPayoutAmount;

    /*
     * Settlement enabled/disabled
     */
    @Builder.Default
    @Column(name = "settlement_enabled", nullable = false)
    private Boolean settlementEnabled = true;

    @Version
    private Long version;
}