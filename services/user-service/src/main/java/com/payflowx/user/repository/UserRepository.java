package com.payflowx.user.repository;

import com.payflowx.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByAuthUserId(UUID authUserId);

    boolean existsByAuthUserId(UUID authUserId);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"profile", "kyc"})
    Optional<User> findWithProfileAndKycByAuthUserId(UUID authUserId);

    @EntityGraph(attributePaths = {"profile", "addresses", "kyc"})
    Optional<User> findCompleteByAuthUserId(UUID authUserId);
}