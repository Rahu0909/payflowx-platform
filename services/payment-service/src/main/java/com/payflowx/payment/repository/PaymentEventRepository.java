package com.payflowx.payment.repository;

import com.payflowx.payment.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, UUID> {

    List<PaymentEvent> findTop100ByProcessedFalseOrderByCreatedAtAsc();
}