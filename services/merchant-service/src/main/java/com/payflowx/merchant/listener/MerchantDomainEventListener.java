package com.payflowx.merchant.listener;

import com.payflowx.merchant.event.MerchantKycApprovedDomainEvent;
import com.payflowx.merchant.event.MerchantKycRejectedDomainEvent;
import com.payflowx.merchant.service.MerchantEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantDomainEventListener {

    private final MerchantEventPublisher merchantEventPublisher;

    @EventListener
    public void handleMerchantKycApproved(MerchantKycApprovedDomainEvent domainEvent) {
        merchantEventPublisher.publishMerchantKycApproved(domainEvent.getMerchantId(), domainEvent.getBusinessName(), domainEvent.getMerchantEmail());
        log.info("Merchant KYC approved integration event published merchantId={}", domainEvent.getMerchantId());
    }

    @EventListener
    public void handleMerchantKycRejected(MerchantKycRejectedDomainEvent domainEvent) {
        merchantEventPublisher.publishMerchantKycRejected(domainEvent.getMerchantId(), domainEvent.getBusinessName(), domainEvent.getMerchantEmail(), domainEvent.getRejectionReason());
        log.info("Merchant KYC rejected integration event published merchantId={}", domainEvent.getMerchantId());
    }
}