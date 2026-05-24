package com.payflowx.auth.repository;

import com.payflowx.auth.entity.RefreshToken;
import com.payflowx.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    List<RefreshToken> findAllByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);
}
