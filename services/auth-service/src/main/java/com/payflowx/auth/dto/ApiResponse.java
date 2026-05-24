package com.payflowx.auth.dto;

import com.payflowx.auth.constant.AppConstants;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        String status,
        String message,
        T data,
        LocalDateTime timestamp) {

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(AppConstants.SUCCESS, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(AppConstants.ERROR, message, data, LocalDateTime.now());
    }
}