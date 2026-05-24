package com.payflowx.auth.controller;

import com.payflowx.auth.dto.ApiResponse;
import com.payflowx.auth.dto.SessionResponse;
import com.payflowx.auth.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping
    public ApiResponse<List<SessionResponse>> getSessions(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.success(sessionService.getActiveSessions(userId), "Sessions fetched successfully");
    }

    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> revokeSession(Authentication authentication, @PathVariable UUID sessionId) {
        UUID userId = UUID.fromString(authentication.getName());
        sessionService.revokeSession(userId, sessionId);
        return ApiResponse.success(null, "Session revoked successfully");
    }

    @PostMapping("/logout-all")
    public ApiResponse<Void> logoutAllDevices(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        sessionService.revokeAllSessions(userId);
        return ApiResponse.success(null, "All sessions revoked successfully");
    }
}