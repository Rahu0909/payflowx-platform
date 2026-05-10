package com.payflowx.payment.repository;

import com.payflowx.payment.entity.PaymentIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentIdempotencyRepository extends JpaRepository<PaymentIdempotency, UUID> {

    Optional<PaymentIdempotency> findByIdempotencyKey(String idempotencyKey);

    List<PaymentIdempotency> findByExpiresAtBefore(LocalDateTime time);
}