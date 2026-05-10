package com.payflowx.payment.validator;

import com.payflowx.payment.enums.PaymentStatus;

public interface PaymentStateMachineValidator {

    void validateTransition(PaymentStatus currentStatus, PaymentStatus targetStatus);

    boolean isRefundable(PaymentStatus status);

    boolean isTerminalState(PaymentStatus status);
}