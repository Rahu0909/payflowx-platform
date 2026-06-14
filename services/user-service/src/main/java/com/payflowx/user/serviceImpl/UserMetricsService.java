package com.payflowx.user.serviceImpl;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMetricsService {

    private final Counter usersCreatedCounter;
    private final Counter kycSubmittedCounter;
    private final Counter kycApprovedCounter;
    private final Counter usersBlockedCounter;

    public void incrementUsersCreated() {
        usersCreatedCounter.increment();
    }

    public void incrementKycSubmitted() {
        kycSubmittedCounter.increment();
    }

    public void incrementKycApproved() {
        kycApprovedCounter.increment();
    }

    public void incrementUsersBlocked() {
        usersBlockedCounter.increment();
    }
}