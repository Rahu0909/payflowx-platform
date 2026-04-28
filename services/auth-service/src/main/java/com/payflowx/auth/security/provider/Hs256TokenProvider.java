package com.payflowx.auth.security.provider;

import com.payflowx.auth.config.JwtProperties;
import com.payflowx.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class Hs256TokenProvider implements TokenProvider {

    private final JwtProperties properties;

    @Override
    public String algorithm() {
        return "HS256";
    }

    @Override
    public String generateToken(User user) {

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(properties.expiration());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                properties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }
}