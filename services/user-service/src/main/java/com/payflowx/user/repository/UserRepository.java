package com.payflowx.user.repository;

import com.payflowx.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Auth-safe
    Optional<User> findByAuthUserIdAndDeletedFalse(UUID authUserId);

    boolean existsByAuthUserId(UUID authUserId);

    boolean existsByEmail(String email);

    // fetch (non-deleted)
    @EntityGraph(attributePaths = {"profile", "kyc"})
    Optional<User> findWithProfileAndKycByAuthUserIdAndDeletedFalse(UUID authUserId);

    @EntityGraph(attributePaths = {"profile", "addresses", "kyc"})
    Optional<User> findCompleteByAuthUserIdAndDeletedFalse(UUID authUserId);

    Optional<User> findByIdAndDeletedFalse(UUID id);

    // Pagination (admin)
    Page<User> findByDeletedFalse(Pageable pageable);
}