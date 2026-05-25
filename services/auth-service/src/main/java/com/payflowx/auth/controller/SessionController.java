package com.payflowx.auth.controller;

import com.payflowx.auth.dto.ApiResponse;
import com.payflowx.auth.dto.SessionResponse;
import com.payflowx.auth.service.SessionService;
import com.payflowx.auth.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping
    public ApiResponse<List<SessionResponse>> getSessions() {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(sessionService.getActiveSessions(userId), "Sessions fetched successfully");
    }

    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> revokeSession(@PathVariable UUID sessionId) {
        UUID userId = SecurityUtil.getCurrentUserId();
        sessionService.revokeSession(userId, sessionId);
        return ApiResponse.success(null, "Session revoked successfully");
    }

    @PostMapping("/logout-all")
    public ApiResponse<Void> logoutAllDevices() {
        UUID userId = SecurityUtil.getCurrentUserId();
        sessionService.revokeAllSessions(userId);
        return ApiResponse.success(null, "All sessions revoked successfully");
    }
}