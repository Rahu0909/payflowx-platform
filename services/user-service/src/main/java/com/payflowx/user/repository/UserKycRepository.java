package com.payflowx.user.repository;

import com.payflowx.user.entity.UserKyc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserKycRepository extends JpaRepository<UserKyc, UUID> {

    Optional<UserKyc> findByUserId(UUID userId);
}