package com.payflowx.auth.serviceImpl;

import com.payflowx.auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void blacklist(String jti, long ttlMillis) {
        if (jti == null || jti.isBlank() || ttlMillis <= 0) {
            log.warn("Invalid blacklist request jti={}, ttl={}", jti, ttlMillis);
            return;
        }
        redisTemplate.opsForValue()
                .set(PREFIX + jti, "true", Duration.ofMillis(ttlMillis));

        log.info("Token blacklisted jti={}, ttl={}ms", jti, ttlMillis);
    }

    @Override
    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
    }
}
