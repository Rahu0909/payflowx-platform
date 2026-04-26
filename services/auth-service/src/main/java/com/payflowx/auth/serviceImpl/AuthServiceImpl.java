package com.payflowx.auth.serviceImpl;

import com.payflowx.auth.constant.AppConstants;
import com.payflowx.auth.constant.AppMessages;
import com.payflowx.auth.dto.*;
import com.payflowx.auth.entity.Role;
import com.payflowx.auth.entity.User;
import com.payflowx.auth.exception.BusinessException;
import com.payflowx.auth.repository.UserRepository;
import com.payflowx.auth.security.JwtService;
import com.payflowx.auth.service.AuthService;
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

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Override
    public ApiResponse<UserResponse> register(UserRequest userRequest) {
        log.info("Register request received for email={}", userRequest.email());

        userRepository.findByEmail(userRequest.email())
                .ifPresent(user -> {
                    log.warn("Duplicate email registration attempt: {}", userRequest.email());
                    throw new BusinessException(AppMessages.EMAIL_EXISTS);
                });

        var user = User.builder()
                .fullName(userRequest.fullName())
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .role(Role.USER)
                .build();

        var savedUser = userRepository.save(user);
        log.info("User registered successfully for Id={}", savedUser.getId());
        var response = new UserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
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
        log.info("Login request received for email={}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AppConstants.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(AppConstants.INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse(
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                token,
                AppConstants.BEARER,
                3600000
        );

        log.info("Login successful for userId={}", user.getId());

        return new ApiResponse<>(
                AppConstants.SUCCESS,
                AppConstants.LOGIN_SUCCESS,
                response,
                LocalDateTime.now()
        );
    }
}
