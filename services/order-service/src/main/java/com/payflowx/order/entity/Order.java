package com.payflowx.order.entity;

import com.payflowx.order.enums.Currency;
import com.payflowx.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_merchant", columnList = "merchant_id"),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_receipt", columnList = "receipt")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    /*
     * Merchant Ownership
     */
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    /*
     * Merchant Snapshot
     * Historical consistency
     */
    @Column(name = "merchant_business_name", nullable = false, length = 150)
    private String merchantBusinessName;

    /*
     * Monetary Details
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 10)
    private Currency currency;

    /*
     * Merchant Reference
     */
    @Column(name = "receipt", nullable = false, unique = true, length = 100)
    private String receipt;

    /*
     * Order Lifecycle
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.CREATED;

    /*
     * Customer Reference
     */
    @Column(name = "customer_email", length = 150)
    private String customerEmail;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    /*
     * Expiry
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /*
     * Payment Tracking
     */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /*
     * Optimistic Locking
     * Critical for concurrency safety
     */
    @Version
    private Long version;
}