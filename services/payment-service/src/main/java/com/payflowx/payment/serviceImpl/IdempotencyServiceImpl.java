package com.payflowx.payment.serviceImpl;

import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.entity.PaymentIdempotency;
import com.payflowx.payment.enums.IdempotencyStatus;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.repository.PaymentIdempotencyRepository;
import com.payflowx.payment.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {
    private final PaymentIdempotencyRepository paymentIdempotencyRepository;

    @Override
    public PaymentIdempotency validateOrGet(String idempotencyKey, String requestHash) {
        PaymentIdempotency existingRecord = paymentIdempotencyRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
        if (existingRecord == null) {
            return null;
        }
        /*
         * Prevent same key with different payload
         */
        if (!existingRecord.getRequestHash().equals(requestHash)) {
            throw new BusinessValidationException(ErrorCode.INVALID_IDEMPOTENCY_REQUEST);
        }
        return existingRecord;
    }

    @Override
    public void saveRecord(String idempotencyKey, String requestHash, UUID paymentId, String responsePayload) {
        PaymentIdempotency record = PaymentIdempotency.builder().idempotencyKey(idempotencyKey).requestHash(requestHash).paymentId(paymentId).responsePayload(responsePayload).status(IdempotencyStatus.COMPLETED).expiresAt(LocalDateTime.now().plusHours(24)).build();
        paymentIdempotencyRepository.save(record);
    }
}