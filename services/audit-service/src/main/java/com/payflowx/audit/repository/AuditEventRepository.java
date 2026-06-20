package com.payflowx.audit.repository;

import com.payflowx.audit.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditEventRepository
        extends JpaRepository<AuditEvent, UUID>,
        JpaSpecificationExecutor<AuditEvent> {

    boolean existsByEventId(String eventId);

    Optional<AuditEvent> findByEventId(String eventId);

    long countBySourceService(String sourceService);
}