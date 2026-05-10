package com.payflowx.payment.repository;

import com.payflowx.payment.entity.PaymentAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, UUID> {

    List<PaymentAttempt> findByPaymentIdOrderByAttemptNumberAsc(UUID paymentId);

    Page<PaymentAttempt> findByPaymentIdOrderByAttemptNumberAsc(UUID paymentId, Pageable pageable);
}