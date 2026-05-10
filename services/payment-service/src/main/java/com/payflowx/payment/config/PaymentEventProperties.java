package com.payflowx.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payment.events")
public class PaymentEventProperties {
    private int batchSize;
    private int retryThreshold;
}