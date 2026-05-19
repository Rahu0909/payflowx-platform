package com.payflowx.merchant.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantKycRejectedEvent extends BaseEvent {

    private String merchantId;

    private String merchantEmail;

    private String businessName;

    private String rejectionReason;
}