package com.payflowx.user.controller;

import com.payflowx.user.dto.ApiResponse;
import com.payflowx.user.dto.InternalUserCreateRequest;
import com.payflowx.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @PostMapping()
    public ApiResponse<Void> createUser(
            @Valid @RequestBody InternalUserCreateRequest request
    ) {
        userService.createUserInternal(
                request.authUserId(),
                request.email()
        );
        return ApiResponse.success(null, "User created");
    }
}