package com.payflowx.gateway.security;

import com.payflowx.gateway.constants.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final RouteValidator routeValidator;
    private final JwtUtil jwtUtil;
    private final RoleBasedAccessValidator accessValidator;
    private final GatewayBlacklistService blacklistService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        var request = exchange.getRequest();
        var path = request.getURI().getPath();

        if (!routeValidator.isSecured.test(path)) {
            return chain.filter(exchange);
        }

        var authHeader =
                request.getHeaders()
                        .getFirst(AppConstants.AUTH_HEADER);

        if (authHeader == null ||
                !authHeader.startsWith(AppConstants.BEARER_PREFIX)) {
            return unauthorized(exchange, "Missing or invalid auth header");
        }

        var token =
                authHeader.substring(
                        AppConstants.BEARER_PREFIX.length()
                );

        if (!jwtUtil.isValid(token)) {
            return unauthorized(exchange, "Invalid token");
        }

        String jti = jwtUtil.extractJti(token);

        if (blacklistService.isBlacklisted(jti)) {
            log.warn("Blocked blacklisted token jti={}", jti);
            return unauthorized(exchange, "Token revoked");
        }

        String role = jwtUtil.extractRole(token);

        if (!accessValidator.hasAccess(path, role)) {
            log.warn("Access denied role={} path={}", role, path);
            return forbidden(exchange);
        }

        log.info("Access granted role={} path={}", role, path);

        return chain.filter(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange,
                                    String message) {

        log.warn(message);

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.FORBIDDEN);

        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}