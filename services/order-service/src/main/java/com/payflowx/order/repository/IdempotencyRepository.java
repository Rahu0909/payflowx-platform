package com.payflowx.order.repository;

import com.payflowx.order.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, UUID> {

    Optional<IdempotencyRecord> findByIdempotencyKey(String key);

    List<IdempotencyRecord> findByExpiresAtBefore(LocalDateTime time);
}