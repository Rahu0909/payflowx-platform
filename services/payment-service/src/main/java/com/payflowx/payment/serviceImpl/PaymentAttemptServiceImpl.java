package com.payflowx.payment.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.dto.response.PaymentGatewayResponse;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.entity.PaymentAttempt;
import com.payflowx.payment.enums.PaymentAttemptStatus;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.repository.PaymentAttemptRepository;
import com.payflowx.payment.service.PaymentAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentAttemptServiceImpl implements PaymentAttemptService {
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PaymentAttempt createAttempt(Payment payment) {
        List<PaymentAttempt> existingAttempts = paymentAttemptRepository.findByPaymentIdOrderByAttemptNumberAsc(payment.getId());
        int nextAttemptNumber = existingAttempts.size() + 1;
        PaymentAttempt attempt = PaymentAttempt.builder().paymentId(payment.getId())
                .attemptNumber(nextAttemptNumber).status(PaymentAttemptStatus.INITIATED).build();
        paymentAttemptRepository.save(attempt);
        log.info("Payment attempt created paymentId={} attemptNumber={}", payment.getId(), nextAttemptNumber);
        return attempt;
    }

    @Override
    @Transactional
    public void markAttemptSuccess(UUID attemptId, PaymentGatewayResponse response, long processingTimeMs) {
        PaymentAttempt attempt = paymentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_ATTEMPT_NOT_FOUND));
        try {
            attempt.setStatus(PaymentAttemptStatus.SUCCESS);
            attempt.setGatewayTransactionId(response.transactionId());
            attempt.setGatewayResponse(objectMapper.writeValueAsString(response));
            attempt.setProcessedAt(LocalDateTime.now());
            attempt.setProcessingTimeMs(processingTimeMs);
            paymentAttemptRepository.save(attempt);
            log.info("Payment attempt success attemptId={} transactionId={}", attempt.getId(), response.transactionId());
        } catch (Exception ex) {
            log.error("Failed to serialize gateway response attemptId={}", attemptId, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Transactional
    public void markAttemptFailed(UUID attemptId, PaymentGatewayResponse response, long processingTimeMs) {
        PaymentAttempt attempt = paymentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_ATTEMPT_NOT_FOUND));
        try {
            attempt.setStatus(PaymentAttemptStatus.FAILED);
            attempt.setFailureReason(response.failureReason());
            attempt.setGatewayResponse(objectMapper.writeValueAsString(response));
            attempt.setProcessedAt(LocalDateTime.now());
            attempt.setProcessingTimeMs(processingTimeMs);
            paymentAttemptRepository.save(attempt);
            log.warn("Payment attempt failed attemptId={} reason={}", attempt.getId(), response.failureReason());
        } catch (Exception ex) {
            log.error("Failed to serialize failed gateway response attemptId={}", attemptId, ex);
            throw new RuntimeException(ex);
        }
    }
}