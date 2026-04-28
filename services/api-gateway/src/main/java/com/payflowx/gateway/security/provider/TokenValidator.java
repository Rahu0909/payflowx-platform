package com.payflowx.gateway.security.provider;

import io.jsonwebtoken.Claims;

public interface TokenValidator {
    String algorithm();
    Claims extractClaims(String token);
    boolean isValid(String token);
}