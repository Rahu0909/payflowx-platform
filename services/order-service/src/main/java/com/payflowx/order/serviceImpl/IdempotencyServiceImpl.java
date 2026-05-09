package com.payflowx.order.serviceImpl;

import com.payflowx.order.entity.IdempotencyRecord;
import com.payflowx.order.repository.IdempotencyRepository;
import com.payflowx.order.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;

    @Override
    public IdempotencyRecord validateOrGet(String idempotencyKey) {
        return idempotencyRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
    }

    @Override
    public void saveRecord(String idempotencyKey, UUID resourceId, String resourceType) {
        IdempotencyRecord record = IdempotencyRecord.builder().idempotencyKey(idempotencyKey).resourceId(resourceId).resourceType(resourceType).expiresAt(LocalDateTime.now().plusHours(24)).build();
        idempotencyRepository.save(record);
    }
}