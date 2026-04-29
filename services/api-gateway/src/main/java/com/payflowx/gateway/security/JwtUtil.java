package com.payflowx.gateway.security;

import io.jsonwebtoken.Claims;
import com.payflowx.gateway.security.provider.TokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final TokenValidator validator;

    public JwtUtil(
            @Value("${app.jwt.algorithm}") String algorithm,
            java.util.List<TokenValidator> validators
    ) {
        Map<String, TokenValidator> map =
                validators.stream()
                        .collect(Collectors.toMap(
                                v -> v.algorithm().toUpperCase(),
                                Function.identity()
                        ));

        this.validator = map.get(algorithm.toUpperCase());

        if (validator == null) {
            throw new IllegalStateException("Unsupported JWT algorithm");
        }
    }

    public Claims extractClaims(String token) {
        return validator.extractClaims(token);
    }

    public boolean isValid(String token) {
        return validator.isValid(token);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractJti(String token) {
        return extractClaims(token).get("jti", String.class);
    }
}