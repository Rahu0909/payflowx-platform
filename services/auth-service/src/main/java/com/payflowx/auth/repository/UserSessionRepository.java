package com.payflowx.auth.repository;

import com.payflowx.auth.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    List<UserSession> findAllByUserIdAndRevokedFalse(UUID userId);

    Optional<UserSession> findByRefreshTokenAndRevokedFalse(String refreshToken);

    List<UserSession> findAllByUserId(UUID userId);
}