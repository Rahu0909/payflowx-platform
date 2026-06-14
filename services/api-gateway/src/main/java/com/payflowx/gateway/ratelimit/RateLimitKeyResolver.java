package com.payflowx.gateway.ratelimit;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class RateLimitKeyResolver {

    public String resolve(String path, Claims claims, String clientIp) {
        if (claims != null) {
            return "user:" + claims.getSubject();
        }
        return "ip:" + clientIp;
    }
}