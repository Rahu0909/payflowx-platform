package com.payflowx.merchant.repository;

import com.payflowx.merchant.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    Optional<Merchant> findByIdAndDeletedFalse(UUID id);

    Optional<Merchant> findByAuthUserIdAndDeletedFalse(UUID authUserId);

    boolean existsByEmail(String email);

    Page<Merchant> findByDeletedFalse(Pageable pageable);

    Page<Merchant> findAllByDeletedFalse(Pageable pageable);
}