package com.payflowx.auth.serviceImpl;

import com.payflowx.auth.dto.SessionResponse;
import com.payflowx.auth.entity.UserSession;
import com.payflowx.auth.exception.BusinessException;
import com.payflowx.auth.repository.UserSessionRepository;
import com.payflowx.auth.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final UserSessionRepository userSessionRepository;

    @Override
    public List<SessionResponse> getActiveSessions(UUID userId) {
        return userSessionRepository.findAllByUserIdAndRevokedFalse(userId).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public void revokeSession(UUID userId, UUID sessionId) {
        UserSession session = userSessionRepository.findById(sessionId).orElseThrow(() -> new BusinessException("Session not found"));
        if (!session.getUser().getId().equals(userId)) {
            throw new BusinessException("Unauthorized session access");
        }
        session.setRevoked(true);
        userSessionRepository.save(session);
    }

    @Override
    @Transactional
    public void revokeAllSessions(UUID userId) {
        List<UserSession> sessions = userSessionRepository.findAllByUserId(userId);
        sessions.forEach(session -> session.setRevoked(true));
        userSessionRepository.saveAll(sessions);
    }

    private SessionResponse mapToResponse(UserSession session) {
        return new SessionResponse(session.getId(), session.getDeviceName(), session.getIpAddress(), session.getUserAgent(), session.getLoginAt(), session.getLastActiveAt(), session.getExpiresAt());
    }
}