package com.payflowx.merchant.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class WebhookSecretUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private WebhookSecretUtil() {
    }

    public static String generateSecret() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return "whsec_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}