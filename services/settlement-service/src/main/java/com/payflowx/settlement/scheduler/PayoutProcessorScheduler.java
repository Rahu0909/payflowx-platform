package com.payflowx.settlement.scheduler;

import com.payflowx.settlement.service.PayoutProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutProcessorScheduler {

    private final PayoutProcessorService payoutProcessorService;

    /*
     * Runs every 1 minute
     */
    @Scheduled(fixedDelay = 60000)
    public void processPayoutsJob() {
        log.debug("Starting payout processor scheduler");
        payoutProcessorService.processPayouts();
    }
}