package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.entity.Payout;
import com.payflowx.settlement.enums.PayoutStatus;
import com.payflowx.settlement.enums.SettlementWebhookEventType;
import com.payflowx.settlement.repository.PayoutRepository;
import com.payflowx.settlement.service.LedgerService;
import com.payflowx.settlement.service.MerchantBalanceService;
import com.payflowx.settlement.service.PayoutProcessorService;
import com.payflowx.settlement.service.SettlementWebhookEventService;
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
public class PayoutProcessorServiceImpl implements PayoutProcessorService {
    private static final int MAX_RETRIES = 3;
    private final PayoutRepository payoutRepository;
    private final MerchantBalanceService merchantBalanceService;
    private final SettlementWebhookEventService webhookEventService;
    private final LedgerService ledgerService;

    @Override
    @Transactional
    public void processPayouts() {
        List<Payout> payouts = payoutRepository.findTop100ByStatusInAndNextRetryAtBefore(List.of(PayoutStatus.PENDING, PayoutStatus.FAILED), LocalDateTime.now());
        if (payouts.isEmpty()) {
            log.debug("No payouts pending processing");
            return;
        }
        payouts.forEach(this::processSinglePayout);
        payoutRepository.saveAll(payouts);
        log.info("Payout processing completed count={}", payouts.size());
    }

    private void processSinglePayout(Payout payout) {
        try {
            payout.setStatus(PayoutStatus.PROCESSING);
            /*
             * MOCK BANK TRANSFER
             */
            boolean bankSuccess = mockBankTransfer();
            if (bankSuccess) {
                merchantBalanceService.deductAvailableBalance(payout.getMerchantId(), payout.getAmount());
                payout.setStatus(PayoutStatus.SUCCESS);
                payout.setProcessedAt(LocalDateTime.now());
                payout.setBankReference("bank_" + UUID.randomUUID());
                payout.setFailureReason(null);
                ledgerService.recordPayoutEntry(
                        payout.getMerchantId(),
                        payout.getId(),
                        payout.getAmount(),
                        payout.getCurrency()
                );
                webhookEventService.publishPayoutEvent(payout, SettlementWebhookEventType.PAYOUT_SUCCESS);
                log.info("Payout successful payoutId={}", payout.getId());
            } else {
                throw new RuntimeException("Bank transfer failed");
            }
        } catch (Exception ex) {
            payout.setRetryCount(payout.getRetryCount() + 1);
            payout.setFailureReason(ex.getMessage());
            if (payout.getRetryCount() >= MAX_RETRIES) {
                payout.setStatus(PayoutStatus.FAILED);
                payout.setNextRetryAt(null);
                webhookEventService.publishPayoutEvent(payout, SettlementWebhookEventType.PAYOUT_FAILED);
                log.error("Payout permanently failed payoutId={}", payout.getId());
            } else {
                payout.setStatus(PayoutStatus.FAILED);
                payout.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
                log.warn("Payout retry scheduled payoutId={} retryCount={}", payout.getId(), payout.getRetryCount());
            }
        }
    }

    /*
     * MOCK BANK SIMULATION
     */
    private boolean mockBankTransfer() {
        return Math.random() > 0.3;
    }
}