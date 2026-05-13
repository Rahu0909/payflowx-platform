package com.payflowx.settlement.scheduler;

import com.payflowx.settlement.service.SettlementReleaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementReleaseScheduler {

    private final SettlementReleaseService settlementReleaseService;

    /*
     * Runs every 1 minute
     */
    @Scheduled(fixedDelay = 60000)
    public void releaseSettlementsJob() {
        log.debug("Starting settlement release scheduler");
        settlementReleaseService.releaseSettlements();
    }
}