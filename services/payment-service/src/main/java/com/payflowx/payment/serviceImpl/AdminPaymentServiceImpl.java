package com.payflowx.payment.serviceImpl;

import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.dto.response.PaymentAttemptResponse;
import com.payflowx.payment.dto.response.PaymentResponse;
import com.payflowx.payment.dto.response.RefundResponse;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.enums.PaymentStatus;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.mapper.PaymentAttemptMapper;
import com.payflowx.payment.mapper.PaymentMapper;
import com.payflowx.payment.mapper.RefundMapper;
import com.payflowx.payment.repository.PaymentAttemptRepository;
import com.payflowx.payment.repository.PaymentRepository;
import com.payflowx.payment.repository.RefundRepository;
import com.payflowx.payment.service.AdminPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPaymentServiceImpl implements AdminPaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final RefundRepository refundRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentAttemptMapper paymentAttemptMapper;
    private final RefundMapper refundMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPayments(PaymentStatus status, Pageable pageable) {
        Page<Payment> payments;
        if (status != null) {
            payments = paymentRepository.findByStatus(status, pageable);
        } else {
            payments = paymentRepository.findAll(pageable);
        }
        return payments.map(paymentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentReference) {
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentAttemptResponse> getAttempts(String paymentReference, Pageable pageable) {
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND));
        var attempts = paymentAttemptRepository.findByPaymentIdOrderByAttemptNumberAsc(payment.getId());
        var responses = attempts.stream().map(paymentAttemptMapper::toResponse).toList();
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RefundResponse> getRefunds(String paymentReference, Pageable pageable) {
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND));
        var refunds = refundRepository.findByPaymentId(payment.getId());
        var responses = refunds.stream().map(refundMapper::toResponse).toList();
        return new PageImpl<>(responses, pageable, responses.size());
    }
}