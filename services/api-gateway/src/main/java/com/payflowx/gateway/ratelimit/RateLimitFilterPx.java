package com.payflowx.gateway.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.distributed.proxy.ProxyManager;
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
public class RateLimitFilterPx implements GlobalFilter, Ordered {
    private final ProxyManager<String> proxyManager;
    private final RateLimitKeyResolver keyResolver;
    private final BucketConfigurationResolver configResolver;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        Claims claims = (Claims) exchange.getAttributes().get("claims");
        String ip = exchange.getRequest().getRemoteAddress() != null ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
        String key = keyResolver.resolve(path, claims, ip);
        Bucket bucket = proxyManager.builder().build(key + ":" + path, () -> configResolver.resolve(path));
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded key={} path={}", key, path);
            return tooManyRequests(exchange);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> tooManyRequests(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = """
                {
                  "status":"FAILURE",
                  "message":"Rate limit exceeded",
                  "data":null
                }
                """;

        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}