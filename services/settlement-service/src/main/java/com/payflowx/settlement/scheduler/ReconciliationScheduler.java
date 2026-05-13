package com.payflowx.settlement.scheduler;

import com.payflowx.settlement.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReconciliationScheduler {

    private final ReconciliationService reconciliationService;

    /*
     * Every 30 minutes
     */
    @Scheduled(fixedDelay = 1800000)
    public void reconcileJob() {
        log.info("Starting reconciliation job");
        reconciliationService.reconcilePayments();
        reconciliationService.reconcileMerchantBalances();
        log.info("Reconciliation job completed");
    }
}