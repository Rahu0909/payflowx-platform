package com.payflowx.merchant.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class MerchantKycApprovedDomainEvent extends ApplicationEvent {
    private final UUID merchantId;
    private final String merchantEmail;
    private final String businessName;

    public MerchantKycApprovedDomainEvent(Object source, UUID merchantId, String merchantEmail, String businessName) {
        super(source);
        this.merchantId = merchantId;
        this.merchantEmail = merchantEmail;
        this.businessName = businessName;
    }
}