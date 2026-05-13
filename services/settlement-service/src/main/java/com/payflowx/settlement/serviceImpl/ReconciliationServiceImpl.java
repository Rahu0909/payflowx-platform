package com.payflowx.settlement.serviceImpl;

import com.payflowx.settlement.entity.MerchantBalance;
import com.payflowx.settlement.entity.Reconciliation;
import com.payflowx.settlement.entity.Settlement;
import com.payflowx.settlement.enums.ReconciliationStatus;
import com.payflowx.settlement.enums.ReconciliationType;
import com.payflowx.settlement.repository.MerchantBalanceRepository;
import com.payflowx.settlement.repository.ReconciliationRepository;
import com.payflowx.settlement.repository.SettlementRepository;
import com.payflowx.settlement.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconciliationServiceImpl implements ReconciliationService {
    private final SettlementRepository settlementRepository;
    private final MerchantBalanceRepository merchantBalanceRepository;
    private final ReconciliationRepository reconciliationRepository;

    @Override
    @Transactional
    public void reconcilePayments() {
        settlementRepository.findAll().forEach(this::reconcileSettlement);
    }

    @Override
    @Transactional
    public void reconcileMerchantBalances() {
        merchantBalanceRepository.findAll().forEach(this::reconcileBalance);
    }

    private void reconcileSettlement(Settlement settlement) {
        try {
            boolean valid = settlement.getAmount().compareTo(BigDecimal.ZERO) > 0;
            Reconciliation reconciliation = Reconciliation.builder().type(ReconciliationType.PAYMENT_SETTLEMENT).
                    status(valid ? ReconciliationStatus.SUCCESS : ReconciliationStatus.MISMATCH).
                    referenceId(settlement.getId()).
                    details(valid ? "Settlement reconciliation successful" : "Invalid settlement amount")
                    .reconciledAt(LocalDateTime.now()).build();
            reconciliationRepository.save(reconciliation);
        } catch (Exception ex) {
            saveFailure(ReconciliationType.PAYMENT_SETTLEMENT, settlement.getId(), ex.getMessage());
        }
    }

    private void reconcileBalance(MerchantBalance balance) {
        try {
            boolean valid = balance.getPendingBalance().compareTo(BigDecimal.ZERO) >= 0
                    &&
                    balance.getAvailableBalance().compareTo(BigDecimal.ZERO) >= 0
                    &&
                    balance.getSettledBalance().compareTo(BigDecimal.ZERO) >= 0;
            Reconciliation reconciliation = Reconciliation.builder().type(ReconciliationType.MERCHANT_BALANCE)
                    .status(valid ? ReconciliationStatus.SUCCESS : ReconciliationStatus.MISMATCH)
                    .referenceId(balance.getId())
                    .details(valid ? "Merchant balance reconciliation successful" : "Negative balance detected")
                    .reconciledAt(LocalDateTime.now()).build();
            reconciliationRepository.save(reconciliation);
        } catch (Exception ex) {
            saveFailure(ReconciliationType.MERCHANT_BALANCE, balance.getId(), ex.getMessage());
        }
    }

    private void saveFailure(ReconciliationType type, java.util.UUID referenceId, String details) {
        reconciliationRepository.save(Reconciliation.builder().type(type).status(ReconciliationStatus.FAILED)
                .referenceId(referenceId).details(details).reconciledAt(LocalDateTime.now()).build());
        log.error("Reconciliation failed type={} referenceId={} details={}", type, referenceId, details);
    }
}