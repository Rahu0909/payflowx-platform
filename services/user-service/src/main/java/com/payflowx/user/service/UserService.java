package com.payflowx.user.service;

import com.payflowx.user.dto.AdminUserResponse;
import com.payflowx.user.dto.UpdateUserRequest;
import com.payflowx.user.dto.UpdateUserStatusRequest;
import com.payflowx.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getCurrentUser(UUID userId);

    void createUserInternal(UUID authUserId, String email);

    UserResponse updateProfile(UUID userId, UpdateUserRequest request);

    Page<AdminUserResponse> getAllUsers(Pageable pageable);

    AdminUserResponse getUserById(UUID userId);

    AdminUserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request);

    void deactivateUser(UUID userId);
}