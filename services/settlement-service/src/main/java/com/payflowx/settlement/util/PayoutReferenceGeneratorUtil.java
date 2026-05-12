package com.payflowx.settlement.util;

import java.util.UUID;

public class PayoutReferenceGeneratorUtil {

    public static String generateReference() {

        return "pout_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}