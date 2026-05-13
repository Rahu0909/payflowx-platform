package com.payflowx.settlement.repository;

import com.payflowx.settlement.entity.Reconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReconciliationRepository extends JpaRepository<Reconciliation, UUID> {
}