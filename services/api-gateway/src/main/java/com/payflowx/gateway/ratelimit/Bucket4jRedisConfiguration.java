package com.payflowx.gateway.ratelimit;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bucket4jRedisConfiguration {

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379");
    }

    @Bean
    public StatefulRedisConnection<String, byte[]> redisConnection(
            RedisClient redisClient) {
        return redisClient.connect(
                io.lettuce.core.codec.RedisCodec.of(
                        io.lettuce.core.codec.StringCodec.UTF8,
                        io.lettuce.core.codec.ByteArrayCodec.INSTANCE)
        );
    }

    @Bean
    public ProxyManager<String> proxyManager(
            StatefulRedisConnection<String, byte[]> connection) {
        return LettuceBasedProxyManager
                .builderFor(connection)
                .build();
    }
}