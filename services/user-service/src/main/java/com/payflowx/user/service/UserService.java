package com.payflowx.user.service;

import com.payflowx.user.dto.UpdateUserRequest;
import com.payflowx.user.dto.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse getCurrentUser(UUID userId);

    void createUserInternal(UUID authUserId, String email);

    UserResponse updateProfile(UUID userId, UpdateUserRequest request);
}