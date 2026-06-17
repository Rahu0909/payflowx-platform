package com.payflowx.gateway.correlation;

import com.payflowx.gateway.constants.CorrelationConstants;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CorrelationIdGenerator {

    public String generate() {
        return CorrelationConstants.CORRELATION_PREFIX
                + UUID.randomUUID();
    }
}