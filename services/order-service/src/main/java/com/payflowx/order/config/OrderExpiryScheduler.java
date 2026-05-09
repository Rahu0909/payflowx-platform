package com.payflowx.order.config;

import com.payflowx.order.service.OrderExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpiryScheduler {

    private final OrderExpiryService orderExpiryService;

    /*
     * Runs every 1 minute
     */
    @Scheduled(fixedDelay = 60000)
    public void expireOrdersJob() {
        log.debug("Starting order expiry scheduler");
        orderExpiryService.expireOrders();
    }
}