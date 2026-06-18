package com.payflowx.settlement.util;

import org.slf4j.MDC;

public final class CorrelationContext {

    private CorrelationContext() {
    }

    public static String getCorrelationId() {
        return MDC.get("correlationId");
    }
}