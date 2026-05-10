package com.payflowx.payment.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@UtilityClass
public class RequestHashUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String generateHash(Object request) {
        try {
            String payload = OBJECT_MAPPER.writeValueAsString(request);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate request hash", ex);
        }
    }
}