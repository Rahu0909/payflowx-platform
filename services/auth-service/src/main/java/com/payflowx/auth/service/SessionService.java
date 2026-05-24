package com.payflowx.auth.service;

import com.payflowx.auth.dto.SessionResponse;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    List<SessionResponse> getActiveSessions(UUID userId);

    void revokeSession(UUID userId, UUID sessionId);

    void revokeAllSessions(UUID userId);
}