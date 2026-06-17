package com.payflowx.gateway.correlation;

import com.payflowx.gateway.constants.CorrelationConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CorrelationIdFilter implements GlobalFilter, Ordered {
    private final CorrelationIdGenerator generator;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CorrelationConstants.CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = generator.generate();
        }
        String path = exchange.getRequest().getURI().getPath();
        MDC.put(CorrelationConstants.MDC_CORRELATION_ID, correlationId);
        ServerHttpRequest request = exchange.getRequest().mutate().header(CorrelationConstants.CORRELATION_ID_HEADER, correlationId).build();
        exchange.getAttributes().put(CorrelationConstants.MDC_CORRELATION_ID, correlationId);
        log.info("Generated correlationId={} path={}", correlationId, path);
        return chain.filter(exchange.mutate().request(request).build()).doFinally(signal -> MDC.remove(CorrelationConstants.MDC_CORRELATION_ID));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}