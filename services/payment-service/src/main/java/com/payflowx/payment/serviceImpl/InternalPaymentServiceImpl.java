package com.payflowx.payment.serviceImpl;

import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.dto.response.InternalPaymentSettlementResponse;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.enums.PaymentStatus;
import com.payflowx.payment.exception.BusinessValidationException;
import com.payflowx.payment.repository.PaymentRepository;
import com.payflowx.payment.service.InternalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalPaymentServiceImpl implements InternalPaymentService {
    private final PaymentRepository paymentRepository;
    @Override
    @Transactional(readOnly = true)
    public InternalPaymentSettlementResponse getSettlementPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessValidationException(ErrorCode.PAYMENT_NOT_FOUND));
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessValidationException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        return new InternalPaymentSettlementResponse(payment.getId(), payment.getMerchantId(), payment.getNetSettlementAmount(), payment.getCurrency().name(), payment.getStatus().name(), payment.getSettlementDelayDays());
    }
}