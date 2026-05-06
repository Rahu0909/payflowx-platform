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
        String userId = jwtUtil.extractClaims(token).getSubject();
        String email = jwtUtil.extractClaims(token).get("email", String.class);

        /*MUTATE REQUEST WITH HEADERS*/
        var mutatedRequest = exchange.getRequest().mutate()
                .header("X-Auth-UserId", userId)
                .header("X-Auth-Email", email)
                .header("X-Auth-Role", role)
                .build();

        var mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        log.warn(message);
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        String body = """
                {
                    "status": "FAILURE",
                    "data": "UNAUTHORIZED",
                    "message": "%s"
                }
                """.formatted(message);
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json");
        String body = """
                {
                    "status": "FAILURE",
                    "data": "FORBIDDEN",
                    "message": "Access denied"
                }
                """;
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}