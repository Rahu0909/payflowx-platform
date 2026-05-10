package com.payflowx.payment.serviceImpl;

import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.dto.request.RefundRequest;
import com.payflowx.payment.dto.response.RefundResponse;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.entity.Refund;
import com.payflowx.payment.enums.PaymentEventType;
import com.payflowx.payment.enums.PaymentStatus;
import com.payflowx.payment.enums.RefundStatus;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.mapper.RefundMapper;
import com.payflowx.payment.repository.PaymentRepository;
import com.payflowx.payment.repository.RefundRepository;
import com.payflowx.payment.service.PaymentEventService;
import com.payflowx.payment.service.RefundService;
import com.payflowx.payment.util.ReferenceGeneratorUtil;
import com.payflowx.payment.validator.PaymentStateMachineValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final RefundMapper refundMapper;
    private final PaymentEventService paymentEventService;
    private final PaymentStateMachineValidator stateMachineValidator;

    @Override
    @Transactional
    public RefundResponse refundPayment(String paymentReference, RefundRequest request) {
        Payment payment = paymentRepository.findByPaymentReferenceForUpdate(paymentReference).orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND));
        /*
         * REFUNDABLE VALIDATION
         */
        if (!stateMachineValidator.isRefundable(payment.getStatus())) {
            throw new BusinessValidationException(ErrorCode.PAYMENT_NOT_REFUNDABLE);
        }
        /*
         * REMAINING REFUND VALIDATION
         */
        BigDecimal remainingAmount = payment.getAmount().subtract(payment.getRefundedAmount());
        if (request.amount().compareTo(remainingAmount) > 0) {
            throw new BusinessValidationException(ErrorCode.REFUND_AMOUNT_EXCEEDED);
        }
        /*
         * CREATE REFUND
         */
        Refund refund = Refund.builder().refundReference(ReferenceGeneratorUtil.generateRefundReference()).paymentId(payment.getId()).amount(request.amount()).reason(request.reason()).status(RefundStatus.SUCCESS).build();
        refundRepository.save(refund);
        /*
         * UPDATE REFUNDED AMOUNT
         */
        BigDecimal updatedRefundedAmount = payment.getRefundedAmount().add(request.amount());
        payment.setRefundedAmount(updatedRefundedAmount);
        /*
         * PAYMENT STATE TRANSITION
         */
        if (updatedRefundedAmount.compareTo(payment.getAmount()) == 0) {
            stateMachineValidator.validateTransition(payment.getStatus(), PaymentStatus.REFUNDED);
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            stateMachineValidator.validateTransition(payment.getStatus(), PaymentStatus.PARTIAL_REFUND);
            payment.setStatus(PaymentStatus.PARTIAL_REFUND);
        }
        paymentRepository.save(payment);
        /*
         * REFUND EVENT
         */
        paymentEventService.publishEvent(payment, PaymentEventType.REFUND_SUCCESS);
        log.info("Refund processed paymentId={} refundId={} amount={}", payment.getId(), refund.getId(), refund.getAmount());
        return refundMapper.toResponse(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> getRefunds(String paymentReference) {
        Payment payment = paymentRepository.findByPaymentReferenceForUpdate(paymentReference)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND));
        return refundRepository.findByPaymentId(payment.getId()).stream().map(refundMapper::toResponse).toList();
    }
}