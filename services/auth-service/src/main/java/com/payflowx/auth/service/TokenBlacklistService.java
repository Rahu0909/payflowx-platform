package com.payflowx.auth.service;

public interface TokenBlacklistService {
    void blacklist(String jti, long ttlMillis);

    boolean isBlacklisted(String jti);
}