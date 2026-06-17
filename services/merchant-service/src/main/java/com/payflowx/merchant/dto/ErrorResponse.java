package com.payflowx.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private String timestamp;

    private String traceId;

    private String errorCode;

    private String message;

    private String path;

    private String service;
}