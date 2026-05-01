package com.payflowx.gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/eureka",
            "/actuator"
    );

    public Predicate<String> isSecured =
            path -> OPEN_API_ENDPOINTS.stream()
                    .noneMatch(path::startsWith);
}
