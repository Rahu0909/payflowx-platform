package com.payflowx.notification.serviceImpl;

import com.payflowx.notification.dto.UserNotificationMessage;
import com.payflowx.notification.service.UserNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    @Override
    public void process(UserNotificationMessage message) {

        log.info("Processing user event type={} userId={}", message.eventType(), message.userId());

        switch (message.eventType()) {

            case "USER_CREATED" -> handleUserCreated(message);

            case "USER_KYC_SUBMITTED" -> handleKycSubmitted(message);

            case "USER_KYC_APPROVED" -> handleKycApproved(message);

            case "USER_KYC_REJECTED" -> handleKycRejected(message);

            case "USER_ACCOUNT_BLOCKED" -> handleAccountBlocked(message);

            case "USER_ACCOUNT_SUSPENDED" -> handleAccountSuspended(message);

            default -> log.warn("Unsupported user event type={}", message.eventType());
        }
    }

    private void handleUserCreated(UserNotificationMessage message) {

        log.info("Handle user created notification email={}", message.email());
    }

    private void handleKycSubmitted(UserNotificationMessage message) {

        log.info("Handle KYC submitted notification userId={}", message.userId());
    }

    private void handleKycApproved(UserNotificationMessage message) {

        log.info("Handle KYC approved notification userId={}", message.userId());
    }

    private void handleKycRejected(UserNotificationMessage message) {

        log.info("Handle KYC rejected notification userId={}", message.userId());
    }

    private void handleAccountBlocked(UserNotificationMessage message) {

        log.warn("Handle account blocked notification userId={}", message.userId());
    }

    private void handleAccountSuspended(UserNotificationMessage message) {

        log.warn("Handle account suspended notification userId={}", message.userId());
    }
}