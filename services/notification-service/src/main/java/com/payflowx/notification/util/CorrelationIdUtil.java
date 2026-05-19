package com.payflowx.notification.util;

import java.util.UUID;

public final class CorrelationIdUtil {

    private CorrelationIdUtil() {
    }

    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}