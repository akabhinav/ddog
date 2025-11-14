package com.observx.collector.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Distributed rate limiting configuration using Bucket4j.
 * Prevents overload and ensures fair resource allocation.
 */
@Configuration
public class RateLimitingConfig {

    /**
     * In-memory bucket cache (can be replaced with Redis for distributed rate limiting)
     */
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    /**
     * Create rate limit bucket for a client
     * Can be extended to per-client, per-API key rate limiting
     */
    public Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, k -> createNewBucket());
    }

    /**
     * Create a new bucket with configured limits:
     * - 10,000 requests per minute
     * - Refill 166 tokens per second (10000/60)
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(
                10000, // capacity
                Refill.intervally(10000, Duration.ofMinutes(1))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Global rate limiter bucket
     */
    @Bean
    public Bucket globalRateLimiter() {
        return createNewBucket();
    }
}
