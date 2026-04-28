package com.payflowx.auth.security.provider;

import com.payflowx.auth.entity.User;

public interface TokenProvider {
    String algorithm();

    String generateToken(User user);
}
