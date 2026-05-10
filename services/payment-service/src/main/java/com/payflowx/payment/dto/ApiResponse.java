package com.payflowx.payment.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(String status, T data, String message, LocalDateTime timestamp) {

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("SUCCESS", data, message, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>("FAILURE", null, message, LocalDateTime.now());
    }
}