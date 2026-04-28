package com.payflowx.auth.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenGenerator {
    public String generateRefreshToken() {
        return UUID.randomUUID() + "-" + UUID.randomUUID();
    }
}
