package com.payflowx.settlement.util;

import java.util.UUID;

public class SettlementReferenceGeneratorUtil {

    public static String generateReference() {
        return "stl_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}