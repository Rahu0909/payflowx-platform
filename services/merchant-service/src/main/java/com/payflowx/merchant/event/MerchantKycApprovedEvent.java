package com.payflowx.merchant.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantKycApprovedEvent extends BaseEvent {

    private String merchantId;

    private String merchantEmail;

    private String businessName;
}