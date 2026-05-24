package com.payflowx.gateway.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class Rs256TokenValidator implements TokenValidator {
    @Value("${app.jwt.public-key}")
    private Resource publicKeyResource;
    private PublicKey publicKey;

    @Override
    public String algorithm() {
        return "RS256";
    }

    @PostConstruct
    public void init() {
        try {
            String key = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            key = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            log.info("RS256 public key loaded successfully");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load RSA public key", ex);
        }
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
    }

    @Override
    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }
}