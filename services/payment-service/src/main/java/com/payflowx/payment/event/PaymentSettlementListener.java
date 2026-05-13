package com.payflowx.payment.event;

import com.payflowx.payment.client.SettlementClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSettlementListener {

    private final SettlementClient settlementClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSettlementCreation(PaymentSuccessSettlementEvent event) {
        try {
            settlementClient.createSettlement(event.paymentId());
            log.info("Settlement triggered paymentId={}", event.paymentId());
        } catch (Exception ex) {
            log.error("Settlement creation failed paymentId={}", event.paymentId(), ex);
        }
    }
}