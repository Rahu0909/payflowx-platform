package com.payflowx.settlement.repository;

import com.payflowx.settlement.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
}