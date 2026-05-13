package com.payflowx.settlement.entity;

import com.payflowx.settlement.enums.ReconciliationStatus;
import com.payflowx.settlement.enums.ReconciliationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reconciliations", indexes =
        {@Index(name = "idx_reconciliation_type", columnList = "type"),
                @Index(name = "idx_reconciliation_status", columnList = "status")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reconciliation extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ReconciliationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReconciliationStatus status;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "reconciled_at", nullable = false)
    private LocalDateTime reconciledAt;
}