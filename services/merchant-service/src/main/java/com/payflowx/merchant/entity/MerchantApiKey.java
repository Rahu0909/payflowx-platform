package com.payflowx.merchant.entity;

import com.payflowx.merchant.enums.ApiKeyEnvironment;
import com.payflowx.merchant.enums.ApiKeyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "merchant_api_keys",
        indexes = {
                @Index(name = "idx_public_key", columnList = "public_key"),
                @Index(name = "idx_key_prefix", columnList = "key_prefix")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantApiKey extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "public_key", nullable = false, unique = true)
    private String publicKey;

    @Column(name = "secret_key_hash", nullable = false)
    private String secretKeyHash;

    @Column(name = "key_prefix", nullable = false)
    private String keyPrefix;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment", nullable = false)
    private ApiKeyEnvironment environment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApiKeyStatus status = ApiKeyStatus.ACTIVE;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by")
    private UUID revokedBy;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}