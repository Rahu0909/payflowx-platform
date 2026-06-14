package com.payflowx.auth.serviceImpl;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMetricsService {

    private final Counter authRegisterCounter;
    private final Counter authLoginSuccessCounter;
    private final Counter authLoginFailedCounter;
    private final Counter authTokenRefreshCounter;
    private final Counter authLogoutCounter;

    public void incrementRegister() {
        authRegisterCounter.increment();
    }

    public void incrementLoginSuccess() {
        authLoginSuccessCounter.increment();
    }

    public void incrementLoginFailure() {
        authLoginFailedCounter.increment();
    }

    public void incrementRefreshToken() {
        authTokenRefreshCounter.increment();
    }

    public void incrementLogout() {
        authLogoutCounter.increment();
    }
}