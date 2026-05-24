package com.payflowx.notification.serviceImpl;

import com.payflowx.notification.dto.AuthNotificationMessage;
import com.payflowx.notification.service.AuthNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthNotificationServiceImpl implements AuthNotificationService {

    @Override
    public void process(AuthNotificationMessage message) {
        log.info("Processing auth event type={} payload={}", message.eventType(), message.payload());
        switch (message.eventType()) {
            case "USER_REGISTERED" -> handleUserRegistered(message);
            case "LOGIN_SUCCESS" -> handleLoginSuccess(message);
            case "PASSWORD_RESET_REQUESTED" -> handlePasswordResetRequested(message);
            case "PASSWORD_RESET_SUCCESS" -> handlePasswordResetSuccess(message);
            case "USER_LOGOUT" -> handleUserLogout(message);
            case "REFRESH_TOKEN_ROTATED" -> handleRefreshTokenRotated(message);
            default -> log.warn("Unsupported auth event type={}", message.eventType());
        }
    }

    private void handleUserRegistered(AuthNotificationMessage message) {
        log.info("Send welcome email payload={}", message.payload());
    }

    private void handleLoginSuccess(AuthNotificationMessage message) {
        log.info("Login notification payload={}", message.payload());
    }

    private void handlePasswordResetRequested(AuthNotificationMessage message) {
        log.info("Password reset email payload={}", message.payload());
    }

    private void handlePasswordResetSuccess(AuthNotificationMessage message) {
        log.info("Password reset success payload={}", message.payload());
    }

    private void handleUserLogout(AuthNotificationMessage message) {
        log.info("Logout notification payload={}", message.payload());
    }

    private void handleRefreshTokenRotated(AuthNotificationMessage message) {
        log.info("Refresh token rotated payload={}", message.payload());
    }
}