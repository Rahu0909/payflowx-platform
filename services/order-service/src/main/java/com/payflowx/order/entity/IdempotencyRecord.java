package com.payflowx.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "idempotency_records", uniqueConstraints = {@UniqueConstraint(name = "uk_idempotency_key", columnNames = "idempotency_key")}, indexes = {@Index(name = "idx_idempotency_key", columnList = "idempotency_key")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}