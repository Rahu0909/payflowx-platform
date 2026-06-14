package com.payflowx.gateway.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class BucketConfigurationResolver {

    public BucketConfiguration resolve(String path) {

        if (path.startsWith("/payments")) {
            return BucketConfiguration.builder().addLimit(Bandwidth.builder().capacity(100).refillGreedy(100, Duration.ofMinutes(1)).build()).build();
        }

        if (path.startsWith("/auth")) {
            return BucketConfiguration.builder().addLimit(Bandwidth.builder().capacity(20).refillGreedy(20, Duration.ofMinutes(1)).build()).build();
        }

        return BucketConfiguration.builder().addLimit(Bandwidth.builder().capacity(300).refillGreedy(300, Duration.ofMinutes(1)).build()).build();
    }
}