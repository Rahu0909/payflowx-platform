package com.payflowx.merchant.util;

import com.payflowx.merchant.enums.ApiKeyEnvironment;

import java.security.SecureRandom;
import java.util.Base64;

public final class ApiKeyGeneratorUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private ApiKeyGeneratorUtil() {}
    public static String generatePublicKey(ApiKeyEnvironment env) {
        String prefix =
                env == ApiKeyEnvironment.LIVE
                        ? "pfx_pk_live_"
                        : "pfx_pk_test_";
        return prefix + randomToken(24);
    }

    public static String generateSecretKey(ApiKeyEnvironment env) {
        String prefix =
                env == ApiKeyEnvironment.LIVE
                        ? "pfx_sk_live_"
                        : "pfx_sk_test_";
        return prefix + randomToken(48);
    }

    public static String extractKeyPrefix(String secretKey) {
        return secretKey.substring(0, 18);
    }

    private static String randomToken(int size) {
        byte[] bytes = new byte[size];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}