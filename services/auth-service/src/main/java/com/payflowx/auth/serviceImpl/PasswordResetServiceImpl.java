package com.payflowx.auth.serviceImpl;

import com.payflowx.auth.constant.ErrorCode;
import com.payflowx.auth.dto.ForgotPasswordRequest;
import com.payflowx.auth.dto.ResetPasswordRequest;
import com.payflowx.auth.entity.PasswordResetToken;
import com.payflowx.auth.entity.RefreshToken;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.enums.AuthEventType;
import com.payflowx.auth.exception.BusinessValidationException;
import com.payflowx.auth.repository.PasswordResetTokenRepository;
import com.payflowx.auth.repository.RefreshTokenRepository;
import com.payflowx.auth.repository.UserRepository;
import com.payflowx.auth.service.AuthEventService;
import com.payflowx.auth.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final int TOKEN_EXPIRY_MINUTES = 15;

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthEventService authEventService;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new BusinessValidationException(ErrorCode.USER_NOT_FOUND));
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder().token(token).user(user).expiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES)).build();
        passwordResetTokenRepository.save(resetToken);
        authEventService.publishEvent(user, AuthEventType.PASSWORD_RESET_REQUESTED, Map.of("userId", user.getId(), "email", user.getEmail(), "token", token));
        log.info("Password reset token generated userId={}", user.getId());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(request.token()).orElseThrow(() -> new BusinessValidationException(ErrorCode.INVALID_RESET_TOKEN));
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessValidationException(ErrorCode.RESET_TOKEN_EXPIRED);
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        resetToken.setUsed(true);
        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);
        /*
         * REVOKE ALL REFRESH TOKENS
         */
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(user.getId());
        refreshTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(refreshTokens);
        authEventService.publishEvent(user, AuthEventType.PASSWORD_RESET_SUCCESS, Map.of("userId", user.getId(), "email", user.getEmail()));
        log.info("Password reset successful userId={}", user.getId());
    }
}