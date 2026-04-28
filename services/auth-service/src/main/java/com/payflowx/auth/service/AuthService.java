package com.payflowx.auth.service;

import com.payflowx.auth.dto.*;

public interface AuthService {
    ApiResponse<UserResponse> register(UserRequest userRequest);

    ApiResponse<LoginResponse> login(LoginRequest request);

    ApiResponse<TokenResponse> refresh(RefreshRequest request);

    ApiResponse<String> logout(LogoutRequest request);
}

