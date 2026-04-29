package com.payflowx.auth.security.provider;

import com.payflowx.auth.entity.User;
import io.jsonwebtoken.Claims;

public interface TokenProvider {
    String algorithm();
    String generateToken(User user);
    Claims parseClaims(String token);
}
