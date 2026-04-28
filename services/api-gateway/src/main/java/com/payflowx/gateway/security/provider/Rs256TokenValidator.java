package com.payflowx.gateway.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class Rs256TokenValidator implements TokenValidator {

    @Value("${app.jwt.public-key}")
    private Resource publicKey;

    @Override
    public String algorithm() {
        return "RS256";
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(loadKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private PublicKey loadKey() {
        try {
            String key = new String(
                    publicKey.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);

            return KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decoded));

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}