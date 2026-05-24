package com.payflowx.auth.serviceImpl;

import com.payflowx.auth.client.UserServiceClient;
import com.payflowx.auth.config.JwtProperties;
import com.payflowx.auth.constant.AppConstants;
import com.payflowx.auth.constant.AppMessages;
import com.payflowx.auth.dto.*;
import com.payflowx.auth.entity.RefreshToken;
import com.payflowx.auth.entity.Role;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.entity.UserSession;
import com.payflowx.auth.enums.AuthEventType;
import com.payflowx.auth.exception.BusinessException;
import com.payflowx.auth.repository.RefreshTokenRepository;
import com.payflowx.auth.repository.UserRepository;
import com.payflowx.auth.repository.UserSessionRepository;
import com.payflowx.auth.security.JwtService;
import com.payflowx.auth.service.AuthEventService;
import com.payflowx.auth.service.AuthService;
import com.payflowx.auth.service.TokenBlacklistService;
import com.payflowx.auth.util.SecurityUtils;
import com.payflowx.auth.util.TokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenGenerator tokenGenerator;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtProperties jwtProperties;
    private final UserServiceClient userServiceClient;
    private final AuthEventService authEventService;

    @Override
    public ApiResponse<UserResponse> register(UserRequest request) {
        log.info("Register request received email={}", request.email());
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new BusinessException(AppMessages.EMAIL_EXISTS);
        });
        User user = User.builder().fullName(request.fullName()).email(request.email()).password(passwordEncoder.encode(request.password())).role(Role.USER).build();
        User saved = userRepository.save(user);
        authEventService.publishEvent(saved, AuthEventType.USER_REGISTERED, Map.of("userId", saved.getId(), "email", saved.getEmail(), "fullName", saved.getFullName()));
        /*
         * USER SERVICE SYNC
         */
        try {
            userServiceClient.createUser(new InternalUserCreateRequest(saved.getId(), saved.getEmail()));
            log.info("User profile synced successfully userId={}", saved.getId());
        } catch (Exception ex) {
            log.error("User-service sync failed userId={}", saved.getId(), ex);
        }
        UserResponse response = new UserResponse(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole().name());
        return new ApiResponse<>(AppConstants.SUCCESS, AppMessages.USER_REGISTERED, response, LocalDateTime.now());
    }

    @Override
    @Transactional
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        log.info("Login request received email={}", request.email());
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new BusinessException(AppConstants.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Invalid password attempt email={}", request.email());
            throw new BusinessException(AppConstants.INVALID_CREDENTIALS);
        }
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = createOrUpdateRefreshToken(user);
        /*
         * CREATE USER SESSION
         */
        createSession(user, refreshToken, request);
        LoginResponse response = new LoginResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), accessToken, refreshToken.getToken(), AppConstants.BEARER, jwtProperties.expiration());
        log.info("Login successful userId={}", user.getId());
        authEventService.publishEvent(user, AuthEventType.LOGIN_SUCCESS, Map.of("userId", user.getId(), "email", user.getEmail()));
        authEventService.publishEvent(user, AuthEventType.SESSION_CREATED, Map.of("userId", user.getId(), "email", user.getEmail()));
        return new ApiResponse<>(AppConstants.SUCCESS, AppConstants.LOGIN_SUCCESS, response, LocalDateTime.now());
    }

    @Override
    @Transactional
    public ApiResponse<TokenResponse> refresh(RefreshRequest request) {
        log.info("Refresh token request initiated");
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken()).orElseThrow(() -> {
            log.error("Refresh token not found");
            return new BusinessException(AppConstants.INVALID_REFRESH_TOKEN);
        });
        if (storedToken.isRevoked()) {
            log.warn("Revoked refresh token used token={}", storedToken.getToken());
            throw new BusinessException(AppConstants.INVALID_REFRESH_TOKEN);
        }
        if (storedToken.getExpiryAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired refresh token used token={}", storedToken.getToken());
            throw new BusinessException(AppConstants.INVALID_REFRESH_TOKEN);
        }
        User user = userRepository.findById(storedToken.getUser().getId()).orElseThrow(() -> new BusinessException("User not found"));
        log.info("Generating new access token userId={}", user.getId());
        String newAccessToken = jwtService.generateToken(user);
        String oldRefreshToken = storedToken.getToken();
        String newRefreshToken = rotateRefreshToken(storedToken);
        /*
         * UPDATE USER SESSION
         */
        userSessionRepository.findByRefreshTokenAndRevokedFalse(oldRefreshToken).ifPresent(session -> {
            session.setRefreshToken(newRefreshToken);
            session.setLastActiveAt(LocalDateTime.now());
            session.setExpiresAt(storedToken.getExpiryAt());
            userSessionRepository.save(session);
            log.info("Session updated sessionId={}", session.getId());
        });
        TokenResponse response = new TokenResponse(newAccessToken, newRefreshToken, AppConstants.BEARER, jwtProperties.expiration());
        authEventService.publishEvent(user, AuthEventType.REFRESH_TOKEN_ROTATED, Map.of("userId", user.getId()));
        return new ApiResponse<>(AppConstants.SUCCESS, AppConstants.TOKEN_REFRESHED, response, LocalDateTime.now());
    }

    @Override
    @Transactional
    public ApiResponse<String> logout(LogoutRequest request) {
        UUID userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("User not found"));
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken()).orElseThrow(() -> new BusinessException(AppConstants.INVALID_REFRESH_TOKEN));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        /*
         * REVOKE SESSION
         */
        userSessionRepository.findByRefreshTokenAndRevokedFalse(request.refreshToken()).ifPresent(session -> {
            session.setRevoked(true);
            userSessionRepository.save(session);
            authEventService.publishEvent(user, AuthEventType.SESSION_REVOKED, Map.of("sessionId", session.getId()));
        });
        log.info("Logout completed userId={}", user.getId());
        authEventService.publishEvent(user, AuthEventType.USER_LOGOUT, Map.of("userId", user.getId(), "email", user.getEmail()));
        return new ApiResponse<>(AppConstants.SUCCESS, AppConstants.LOGOUT_SUCCESS, "Logged out successfully", LocalDateTime.now());
    }

    @Transactional
    @Override
    public ApiResponse<String> changePassword(ChangePasswordRequest request) {
        log.info("Change password request initiated");
        UUID userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("User not found"));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException("New password cannot be same as current password");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        /*
         * REVOKE ALL REFRESH TOKENS
         */
        refreshTokenRepository.deleteAllByUserId(user.getId());
        /*
         * REVOKE ALL SESSIONS
         */
        userSessionRepository.findAllByUserId(user.getId()).forEach(session -> {
            session.setRevoked(true);
            userSessionRepository.save(session);
        });
        authEventService.publishEvent(user, AuthEventType.PASSWORD_CHANGED, Map.of("userId", user.getId(), "email", user.getEmail()));
        authEventService.publishEvent(user, AuthEventType.ALL_SESSIONS_REVOKED, Map.of("userId", user.getId()));
        log.info("Password changed successfully userId={}", user.getId());
        return ApiResponse.success(AppConstants.PASSWORD_CHANGED, AppConstants.PASSWORD_CHANGED);
    }

    /*
     * PRIVATE METHODS
     */
    private RefreshToken createOrUpdateRefreshToken(User user) {
        String tokenValue = tokenGenerator.generateRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(RefreshToken.builder().user(user).build());
        refreshToken.setToken(tokenValue);
        refreshToken.setExpiryAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    private String rotateRefreshToken(RefreshToken token) {
        String newToken = tokenGenerator.generateRefreshToken();
        token.setToken(newToken);
        token.setExpiryAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS));
        token.setRevoked(false);
        refreshTokenRepository.save(token);
        return newToken;
    }

    private void createSession(User user, RefreshToken refreshToken, LoginRequest request) {
        UserSession session = UserSession.builder().user(user).refreshToken(refreshToken.getToken()).deviceName(request.deviceName()).ipAddress(request.ipAddress()).userAgent(request.userAgent()).loginAt(LocalDateTime.now()).lastActiveAt(LocalDateTime.now()).expiresAt(refreshToken.getExpiryAt()).revoked(false).build();
        userSessionRepository.save(session);
        log.info("User session created sessionId={} userId={}", session.getId(), user.getId());
    }
}