package com.payflowx.auth.client;

import com.payflowx.auth.dto.InternalUserCreateRequest;
import com.payflowx.auth.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/internal/users")
    ApiResponse<Void> createUser(InternalUserCreateRequest request);
}