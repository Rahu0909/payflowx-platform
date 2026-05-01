package com.payflowx.user.controller;

import com.payflowx.user.dto.ApiResponse;
import com.payflowx.user.dto.UpdateUserRequest;
import com.payflowx.user.dto.UserResponse;
import com.payflowx.user.service.UserService;
import com.payflowx.user.util.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getCurrentUser(HttpServletRequest request) {
        UUID userId = HeaderUtil.extractUserId(request);
        UserResponse response = userService.getCurrentUser(userId);
        return ApiResponse.success(response, "Profile Fetched Successfully");
    }


    @PutMapping("/profile")
    public ApiResponse<UserResponse> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateUserRequest requestBody
    ) {
        UUID userId = HeaderUtil.extractUserId(request);
        UserResponse response = userService.updateProfile(userId, requestBody);
        return ApiResponse.success(response, "Profile updated successfully");
    }
}