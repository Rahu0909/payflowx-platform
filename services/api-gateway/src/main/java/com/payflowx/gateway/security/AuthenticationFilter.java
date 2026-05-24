package com.payflowx.gateway.security;

import com.payflowx.gateway.constants.AppConstants;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        String path = request.getURI().getPath();
        /*
         * SKIP SECURITY FOR PUBLIC ROUTES
         */
        if (!routeValidator.isSecured(path)) {
            log.debug("Public route accessed path={}", path);
            return chain.filter(exchange);
        }
        /*
         * EXTRACT AUTH HEADER
         */
        String authHeader = request.getHeaders().getFirst(AppConstants.AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(AppConstants.BEARER_PREFIX)) {
            log.warn("Missing or invalid authorization header path={}", path);
            return unauthorized(exchange, "Missing or invalid authorization header");
        }
        /*
         * EXTRACT JWT TOKEN
         */
        String token = authHeader.substring(AppConstants.BEARER_PREFIX.length());
        /*
         * VALIDATE TOKEN
         */
        if (!jwtUtil.isValid(token)) {
            log.warn("Invalid JWT token path={}", path);
            return unauthorized(exchange, "Invalid or expired token");
        }
        /*
         * PARSE CLAIMS ONLY ONCE
         */
        Claims claims = jwtUtil.extractClaims(token);
        String userId = claims.getSubject();
        String role = claims.get(AppConstants.ROLE, String.class);
        String email = claims.get("email", String.class);
        String jti = claims.get("jti", String.class);
        /*
         * CHECK TOKEN BLACKLIST
         */
        if (blacklistService.isBlacklisted(jti)) {
            log.warn("Blocked blacklisted token jti={} userId={}", jti, userId);
            return unauthorized(exchange, "Token revoked");
        }
        /*
         * ROLE-BASED ACCESS CONTROL
         */
        if (!accessValidator.hasAccess(path, role)) {
            log.warn("Access denied role={} path={} userId={}", role, path, userId);
            return forbidden(exchange);
        }
        /*
         * SUCCESS LOG
         */
        log.info("Access granted userId={} role={} path={}", userId, role, path);
        /*
         * FORWARD USER CONTEXT TO MICROSERVICES
         */
        var mutatedRequest = request.mutate().header("X-Auth-UserId", userId).header("X-Auth-Email", email).header("X-Auth-Role", role).header("X-Auth-Jti", jti).header("X-Request-Source", "api-gateway").build();
        var mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        return chain.filter(mutatedExchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = """
                {
                    "status": "FAILURE",
                    "message": "%s",
                    "data": null
                }
                """.formatted(message);
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = """
                {
                    "status": "FAILURE",
                    "message": "Access denied",
                    "data": null
                }
                """;
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}