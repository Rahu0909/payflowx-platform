package com.payflowx.user.controller;

import com.payflowx.user.dto.AdminUserResponse;
import com.payflowx.user.dto.ApiResponse;
import com.payflowx.user.dto.UpdateUserStatusRequest;
import com.payflowx.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(
                userService.getAllUsers(PageRequest.of(page, size)),
                "Users fetched successfully"
        );
    }

    @GetMapping("/{userId}")
    public ApiResponse<AdminUserResponse> getUser(@PathVariable UUID userId) {
        return ApiResponse.success(
                userService.getUserById(userId),
                "User fetched successfully"
        );
    }

    @PatchMapping("/{userId}/status")
    public ApiResponse<AdminUserResponse> updateStatus(
            @PathVariable UUID userId,
            @RequestBody UpdateUserStatusRequest request
    ) {
        return ApiResponse.success(
                userService.updateUserStatus(userId, request),
                "User status updated successfully"
        );
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deactivateUser(@PathVariable UUID userId) {
        userService.deactivateUser(userId);
        return ApiResponse.success(null, "User deactivated");
    }
}