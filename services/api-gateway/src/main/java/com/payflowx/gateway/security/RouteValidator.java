package com.payflowx.gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteValidator {

    private static final List<String> OPEN_API_ENDPOINTS = List.of(

            "/auth/login", "/auth/register", "/auth/refresh",

            "/auth/password/forgot", "/auth/password/reset",

            "/actuator", "/eureka",

            "/public/");

    public boolean isSecured(String path) {
        return OPEN_API_ENDPOINTS.stream().noneMatch(path::startsWith);
    }
}