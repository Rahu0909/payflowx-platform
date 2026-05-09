package com.payflowx.order.repository;

import com.payflowx.order.entity.Order;
import com.payflowx.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByReceipt(String receipt);

    Page<Order> findByMerchantId(UUID merchantId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByExpiresAtBeforeAndStatus(LocalDateTime time, OrderStatus status, Pageable pageable);
}