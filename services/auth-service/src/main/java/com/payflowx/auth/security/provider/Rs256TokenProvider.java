package com.payflowx.auth.security.provider;

import com.payflowx.auth.config.JwtProperties;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.util.RsaKeyUtil;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

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

            return Jwts.builder()
                    .subject(user.getEmail())
                    .claim("role", user.getRole().name())
                    .claim("userId", user.getId())
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
}