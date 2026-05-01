package com.payflowx.auth.security.provider;

import com.payflowx.auth.config.JwtProperties;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.util.RsaKeyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Rs256TokenProvider implements TokenProvider {

    private final JwtProperties properties;
    private final RsaKeyUtil rsaKeyUtil;

    @Override
    public String algorithm() {
        return "RS256";
    }

    @Override
    public String generateToken(User user) {

        try {
            Instant now = Instant.now();
            Instant expiry = now.plusMillis(properties.expiration());
            String jti = UUID.randomUUID().toString();

            return Jwts.builder()
                    .subject(user.getId().toString())
                    .claim("email", user.getEmail())
                    .claim("role", user.getRole().name())
                    .claim("jti", jti)

                    .issuedAt(Date.from(now))
                    .expiration(Date.from(expiry))
                    .signWith(
                            rsaKeyUtil.loadPrivateKey(properties.privateKey()),
                            Jwts.SIG.RS256
                    )
                    .compact();

        } catch (Exception ex) {
            throw new RuntimeException("RS256 token generation failed", ex);
        }
    }

    @Override
    public Claims parseClaims(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(
                            rsaKeyUtil.loadPublicKey(properties.publicKey())
                    )
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception ex) {
            throw new RuntimeException("RS256 token validation failed", ex);
        }
    }
}