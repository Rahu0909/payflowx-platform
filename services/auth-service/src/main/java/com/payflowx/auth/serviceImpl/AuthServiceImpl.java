package com.payflowx.auth.serviceImpl;

import com.payflowx.auth.client.UserServiceClient;
import com.payflowx.auth.config.JwtProperties;
import com.payflowx.auth.constant.AppConstants;
import com.payflowx.auth.constant.AppMessages;
import com.payflowx.auth.dto.*;
import com.payflowx.auth.entity.RefreshToken;
import com.payflowx.auth.entity.Role;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.exception.BusinessException;
import com.payflowx.auth.repository.RefreshTokenRepository;
import com.payflowx.auth.repository.UserRepository;
import com.payflowx.auth.security.JwtService;
import com.payflowx.auth.service.AuthService;
import com.payflowx.auth.service.TokenBlacklistService;
import com.payflowx.auth.util.TokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final TokenGenerator tokenGenerator;

    private final TokenBlacklistService tokenBlacklistService;

    private final JwtProperties jwtProperties;

    private final UserServiceClient userServiceClient;

    @Override
    public ApiResponse<UserResponse> register(UserRequest request) {

        log.info("Register request for email={}", request.email());

        userRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new BusinessException(AppMessages.EMAIL_EXISTS);
                });

        var user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        var saved = userRepository.save(user);
        /* CALL USER SERVICE*/
        try {
            userServiceClient.createUser(
                    new InternalUserCreateRequest(
                            saved.getId(),
                            saved.getEmail()
                    )
            );
            log.info("User profile created in user-service for userId={}", saved.getId());

        } catch (Exception ex) {
            log.error("User service sync failed for userId={}", saved.getId(), ex);
            // Decide strategy:
            // Option 1: fail registration
            // Option 2: allow retry later
        }
        var response = new UserResponse(
                saved.getId(),   // now UUID
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole().name()
        );

        return new ApiResponse<>(
                AppConstants.SUCCESS,
                AppMessages.USER_REGISTERED,
                response,
                LocalDateTime.now()
        );
    }

    @Transactional
    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BusinessException(AppConstants.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(AppConstants.INVALID_CREDENTIALS);
        }
        var accessToken = jwtService.generateToken(user);
        var refreshToken = createOrUpdateRefreshToken(user);
        var expiry = jwtProperties.expiration();
        var response = new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken,
                AppConstants.BEARER,
                expiry
        );
        log.info("Login success userId={}", user.getId());
        return new ApiResponse<>(
                AppConstants.SUCCESS,
                AppConstants.LOGIN_SUCCESS,
                response,
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse<TokenResponse> refresh(RefreshRequest request) {

        var storedToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() ->
                        new BusinessException(AppConstants.INVALID_REFRESH_TOKEN));
        if (storedToken.isRevoked()
                || storedToken.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(AppConstants.INVALID_REFRESH_TOKEN);
        }
        var user = storedToken.getUser();
        var newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = rotateRefreshToken(storedToken);
        var expiry = jwtProperties.expiration();
        var response = new TokenResponse(
                newAccessToken,
                newRefreshToken,
                AppConstants.BEARER,
                expiry
        );
        log.info("Token refreshed for userId={}", user.getId());
        return new ApiResponse<>(
                AppConstants.SUCCESS,
                AppConstants.TOKEN_REFRESHED,
                response,
                LocalDateTime.now()
        );
    }

    private String createOrUpdateRefreshToken(User user) {
        var tokenValue = tokenGenerator.generateRefreshToken();
        var refreshToken = refreshTokenRepository
                .findByUser(user)
                .orElse(
                        RefreshToken.builder()
                                .user(user)
                                .build()
                );
        refreshToken.setToken(tokenValue);
        refreshToken.setExpiryAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }

    private String rotateRefreshToken(RefreshToken token) {
        var newToken = tokenGenerator.generateRefreshToken();
        token.setToken(newToken);
        token.setExpiryAt(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);
        refreshTokenRepository.save(token);
        return newToken;
    }

    @Override
    public ApiResponse<String> logout(String authHeader,
                                      LogoutRequest request) {

        // =========================
        // Revoke Refresh Token
        // =========================
        var refreshToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() ->
                        new BusinessException(
                                AppConstants.INVALID_REFRESH_TOKEN
                        ));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // =========================
        // Blacklist Access Token
        // =========================
        if (!authHeader.startsWith(AppConstants.BEARER_PREFIX)) {
            throw new BusinessException("Invalid Authorization header");
        }
        String rawToken =
                authHeader.replace(
                        AppConstants.BEARER_PREFIX,
                        ""
                );

        String jti = jwtService.extractJti(rawToken);

        long ttl =
                jwtService.extractExpiration(rawToken)
                        .toInstant()
                        .toEpochMilli()
                        - System.currentTimeMillis();

        if (ttl > 0) {
            tokenBlacklistService.blacklist(jti, ttl);
        }

        log.info("Logout completed for userId={}",
                refreshToken.getUser().getId());

        return new ApiResponse<>(
                AppConstants.SUCCESS,
                AppConstants.LOGOUT_SUCCESS,
                "Logged out successfully",
                LocalDateTime.now()
        );
    }
}
