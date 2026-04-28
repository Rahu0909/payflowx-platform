package com.payflowx.auth.serviceImpl;

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
import com.payflowx.auth.util.TokenGenerator;
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

        var response = new UserResponse(
                saved.getId(),
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
        var response = new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken,
                AppConstants.BEARER,
                3600000
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
        var response = new TokenResponse(
                newAccessToken,
                newRefreshToken,
                AppConstants.BEARER,
                3600000
        );
        log.info("Token refreshed for userId={}", user.getId());
        return new ApiResponse<>(
                AppConstants.SUCCESS,
                AppConstants.TOKEN_REFRESHED,
                response,
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse<String> logout(LogoutRequest request) {
        var token = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() ->
                        new BusinessException(AppConstants.INVALID_REFRESH_TOKEN));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
        log.info("Logout successful for userId={}", token.getUser().getId());
        return new ApiResponse<>(
                AppConstants.SUCCESS,
                AppConstants.LOGOUT_SUCCESS,
                "Logged out",
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
}
