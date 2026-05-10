package com.payflowx.payment.processor;

import com.payflowx.payment.dto.response.PaymentGatewayResponse;
import com.payflowx.payment.entity.Payment;
import com.payflowx.payment.entity.PaymentAttempt;
import com.payflowx.payment.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WalletPaymentProcessor extends AbstractPaymentProcessor {

    @Override
    public PaymentGatewayResponse process(Payment payment, PaymentAttempt attempt) {
        log.info("Processing WALLET payment paymentId={}", payment.getId());
        simulateProcessingDelay();
        if (randomSuccess()) {
            return successResponse();
        }
        return failedResponse("Wallet payment failed");
    }

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.WALLET;
    }
}