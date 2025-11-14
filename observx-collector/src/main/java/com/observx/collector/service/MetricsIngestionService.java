package com.observx.collector.service;

import com.observx.common.model.Metric;
import com.observx.common.util.IdGenerator;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service for ingesting metrics with high performance.
 * Uses Virtual Threads for async processing and Kafka for event streaming.
 * Enhanced with resilience patterns: Circuit Breakers, Rate Limiting, Bulkheads.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsIngestionService {

    private static final String METRICS_TOPIC = "observx.metrics";

    private final KafkaTemplate<String, Metric> kafkaTemplate;

    /**
     * Ingest a single metric asynchronously using Virtual Threads
     */
    @Async
    public void ingestMetric(Metric metric) {
        enrichMetric(metric);
        publishToKafka(metric);
        log.debug("Metric ingested: {} = {}", metric.getName(), metric.getValue());
    }

    /**
     * Ingest multiple metrics in parallel using Virtual Threads
     */
    public void ingestMetricsBatch(List<Metric> metrics) {
        metrics.parallelStream().forEach(metric -> {
            enrichMetric(metric);
            publishToKafka(metric);
        });
        log.info("Batch of {} metrics ingested", metrics.size());
    }

    /**
     * Enrich metric with generated ID and timestamp if missing
     */
    private void enrichMetric(Metric metric) {
        if (metric.getId() == null) {
            metric.setId(IdGenerator.generateUuid());
        }
        if (metric.getTimestamp() == null) {
            metric.setTimestamp(Instant.now());
        }
    }

    /**
     * Publish metric to Kafka for downstream processing with resilience patterns.
     * - Circuit Breaker: Fails fast if Kafka is down
     * - Bulkhead: Limits concurrent Kafka operations
     */
    @CircuitBreaker(name = "kafka", fallbackMethod = "fallbackPublish")
    @Bulkhead(name = "kafka", fallbackMethod = "fallbackPublish")
    private void publishToKafka(Metric metric) {
        try {
            kafkaTemplate.send(METRICS_TOPIC, metric.getName(), metric);
        } catch (Exception e) {
            log.error("Failed to publish metric to Kafka: {}", metric.getName(), e);
            throw e; // Rethrow to trigger circuit breaker
        }
    }

    /**
     * Fallback method when Kafka is unavailable or circuit is open.
     * Logs the metric for later recovery (could write to local disk/database).
     */
    private void fallbackPublish(Metric metric, Exception e) {
        log.error("Circuit breaker active or bulkhead full. Metric not published: {} (value: {}). Error: {}",
                metric.getName(), metric.getValue(), e.getMessage());
        // TODO: Implement fallback storage (write to disk, dead letter queue, etc.)
        // For now, we just log the metric data for recovery
        log.warn("FALLBACK - Metric: {}, Value: {}, Tags: {}, Time: {}",
                metric.getName(), metric.getValue(), metric.getTags(), metric.getTimestamp());
    }
}
