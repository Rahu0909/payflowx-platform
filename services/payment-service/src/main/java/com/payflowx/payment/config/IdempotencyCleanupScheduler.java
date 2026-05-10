package com.payflowx.payment.config;

import com.payflowx.payment.repository.PaymentIdempotencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyCleanupScheduler {
    private final PaymentIdempotencyRepository repository;

    @Scheduled(fixedDelay = 3600000)
    public void cleanupExpiredRecords() {
        var expiredRecords = repository.findByExpiresAtBefore(LocalDateTime.now());
        if (expiredRecords.isEmpty()) {
            return;
        }
        repository.deleteAll(expiredRecords);
        log.info("Expired idempotency records deleted count={}", expiredRecords.size());
    }
}