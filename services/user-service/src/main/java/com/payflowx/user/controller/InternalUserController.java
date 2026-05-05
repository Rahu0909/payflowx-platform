package com.payflowx.user.controller;

import com.payflowx.user.dto.ApiResponse;
import com.payflowx.user.dto.EligibilityResponse;
import com.payflowx.user.dto.InternalUserCreateRequest;
import com.payflowx.user.service.UserService;
import com.payflowx.user.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;
    private final UserValidationService validationService;

    @PostMapping
    public ApiResponse<Void> createUser(@RequestBody InternalUserCreateRequest request) {
        userService.createUserInternal(request.authUserId(), request.email());
        return ApiResponse.success(null, "User created");
    }

    @GetMapping("/{userId}/eligibility")
    public ApiResponse<EligibilityResponse> checkEligibility(@PathVariable UUID userId) {
        return ApiResponse.success(
                validationService.validateUserForPayment(userId),
                "Eligibility checked"
        );
    }

    @GetMapping("/{userId}/validate")
    public ApiResponse<Void> validateOrThrow(@PathVariable UUID userId) {
        validationService.validateOrThrow(userId);
        return ApiResponse.success(null, "User is eligible");
    }
}