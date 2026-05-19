package com.payflowx.merchant.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class MerchantKycRejectedDomainEvent extends ApplicationEvent {

    private final UUID merchantId;

    private final String merchantEmail;

    private final String businessName;

    private final String rejectionReason;

    public MerchantKycRejectedDomainEvent(Object source, UUID merchantId, String merchantEmail, String businessName, String rejectionReason) {
        super(source);
        this.merchantId = merchantId;
        this.merchantEmail = merchantEmail;
        this.businessName = businessName;
        this.rejectionReason = rejectionReason;
    }
}