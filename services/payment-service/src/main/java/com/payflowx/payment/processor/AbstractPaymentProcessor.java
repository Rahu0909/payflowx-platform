package com.payflowx.payment.processor;

import com.payflowx.payment.dto.response.PaymentGatewayResponse;

import java.util.UUID;

public abstract class AbstractPaymentProcessor implements PaymentProcessor {

    protected PaymentGatewayResponse successResponse() {
        return new PaymentGatewayResponse(true, "gw_" + UUID.randomUUID()
                , "txn_" + UUID.randomUUID(),
                null);
    }

    protected PaymentGatewayResponse failedResponse(String reason) {
        return new PaymentGatewayResponse(false, null, null, reason);
    }

    protected void simulateProcessingDelay() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    protected boolean randomSuccess() {
        return Math.random() > 0.2;
    }
}