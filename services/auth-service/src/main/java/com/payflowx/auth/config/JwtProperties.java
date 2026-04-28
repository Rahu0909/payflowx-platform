package com.payflowx.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String algorithm,
        String secret,
        String privateKey,
        String publicKey,
        long expiration
) {
}
