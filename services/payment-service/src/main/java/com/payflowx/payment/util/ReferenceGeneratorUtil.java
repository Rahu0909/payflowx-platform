package com.payflowx.payment.util;

import java.util.UUID;

public class ReferenceGeneratorUtil {

    private ReferenceGeneratorUtil() {
    }

    public static String generatePaymentReference() {
        return "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static String generateRefundReference() {
        return "refund_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}