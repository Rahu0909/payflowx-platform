package com.payflowx.auth.repository;

import com.payflowx.auth.entity.AuthEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuthEventRepository extends JpaRepository<AuthEvent, UUID> {

    List<AuthEvent> findTop100ByProcessedFalseOrderByCreatedAtAsc();
}