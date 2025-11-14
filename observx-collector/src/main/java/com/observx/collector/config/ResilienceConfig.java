package com.observx.collector.config;

import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j configuration for fault tolerance patterns:
 * - Circuit Breakers: Prevent cascading failures
 * - Rate Limiters: Control ingestion rate
 * - Bulkheads: Isolate resources
 */
@Configuration
public class ResilienceConfig {

    /**
     * Circuit breaker configuration for Kafka producer
     * Prevents overwhelming Kafka when it's slow or down
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Open circuit if 50% fail
                .slowCallRateThreshold(50) // Also consider slow calls
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait 30s before retry
                .permittedNumberOfCallsInHalfOpenState(10)
                .minimumNumberOfCalls(5)
                .slidingWindowSize(10)
                .build();

        return CircuitBreakerRegistry.of(config);
    }

    /**
     * Rate limiter configuration to prevent overload
     * Limits requests per second globally
     */
    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(10000) // 10K requests per second per instance
                .timeoutDuration(Duration.ofMillis(100))
                .build();

        return RateLimiterRegistry.of(config);
    }

    /**
     * Bulkhead configuration to limit concurrent executions
     * Prevents thread pool exhaustion
     */
    @Bean
    public BulkheadRegistry bulkheadRegistry() {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(1000) // Max 1000 concurrent operations
                .maxWaitDuration(Duration.ofMillis(500))
                .build();

        return BulkheadRegistry.of(config);
    }
}
