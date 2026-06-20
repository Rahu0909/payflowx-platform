package com.payflowx.audit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "audit_events", indexes = {@Index(name = "idx_audit_event_id", columnList = "event_id"), @Index(name = "idx_audit_correlation", columnList = "correlation_id"), @Index(name = "idx_audit_aggregate", columnList = "aggregate_id"), @Index(name = "idx_audit_type", columnList = "event_type"), @Index(name = "idx_audit_created", columnList = "created_at")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "correlation_id", nullable = false)
    private String correlationId;

    @Column(name = "aggregate_id")
    private String aggregateId;

    @Column(name = "source_service", nullable = false)
    private String sourceService;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;
}