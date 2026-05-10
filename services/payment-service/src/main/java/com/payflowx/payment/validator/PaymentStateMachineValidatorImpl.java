package com.payflowx.payment.validator;

import com.payflowx.payment.constant.ErrorCode;
import com.payflowx.payment.enums.PaymentStatus;
import com.payflowx.payment.exception.BusinessValidationException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class PaymentStateMachineValidatorImpl implements PaymentStateMachineValidator {

    private static final Map<PaymentStatus, Set<PaymentStatus>> VALID_TRANSITIONS = Map.of(PaymentStatus.CREATED, Set.of(PaymentStatus.PROCESSING), PaymentStatus.PROCESSING, Set.of(PaymentStatus.AUTHORIZED, PaymentStatus.SUCCESS, PaymentStatus.FAILED, PaymentStatus.EXPIRED), PaymentStatus.AUTHORIZED, Set.of(PaymentStatus.SUCCESS, PaymentStatus.FAILED), PaymentStatus.SUCCESS, Set.of(PaymentStatus.PARTIAL_REFUND, PaymentStatus.REFUNDED), PaymentStatus.PARTIAL_REFUND, Set.of(PaymentStatus.REFUNDED));

    @Override
    public void validateTransition(PaymentStatus currentStatus, PaymentStatus targetStatus) {
        Set<PaymentStatus> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);
        if (allowedTransitions == null || !allowedTransitions.contains(targetStatus)) {
            throw new BusinessValidationException(ErrorCode.INVALID_PAYMENT_STATE_TRANSITION);
        }
    }

    @Override
    public boolean isRefundable(PaymentStatus status) {
        return status == PaymentStatus.SUCCESS || status == PaymentStatus.PARTIAL_REFUND;
    }

    @Override
    public boolean isTerminalState(PaymentStatus status) {
        return status == PaymentStatus.SUCCESS ||
                status == PaymentStatus.FAILED ||
                status == PaymentStatus.EXPIRED ||
                status == PaymentStatus.REFUNDED;
    }
}