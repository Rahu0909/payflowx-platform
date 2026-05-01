package com.payflowx.auth.security;

import com.payflowx.auth.config.JwtProperties;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.security.provider.TokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final TokenProvider activeProvider;
    private final JwtProperties jwtProperties;

    public JwtService(
            JwtProperties properties,
            List<TokenProvider> providers
    ) {

        this.jwtProperties = properties;

        Map<String, TokenProvider> providerMap =
                providers.stream()
                        .collect(Collectors.toMap(
                                provider -> provider.algorithm().toUpperCase(),
                                Function.identity()
                        ));

        this.activeProvider =
                providerMap.get(
                        properties.algorithm().toUpperCase()
                );

        if (this.activeProvider == null) {
            throw new IllegalStateException(
                    "Unsupported JWT algorithm: " + properties.algorithm()
            );
        }
    }

    public String generateToken(User user) {
        return activeProvider.generateToken(user);
    }

    public Claims extractAllClaims(String token) {
        return activeProvider.parseClaims(token);
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractJti(String token) {
        return extractAllClaims(token).get("jti", String.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String currentAlgorithm() {
        return jwtProperties.algorithm();
    }
}