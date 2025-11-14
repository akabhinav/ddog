package com.observx.collector.service;

import com.observx.common.model.Metric;
import com.observx.common.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service for ingesting metrics with high performance.
 * Uses Virtual Threads for async processing and Kafka for event streaming.
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
     * Publish metric to Kafka for downstream processing
     */
    private void publishToKafka(Metric metric) {
        try {
            kafkaTemplate.send(METRICS_TOPIC, metric.getName(), metric);
        } catch (Exception e) {
            log.error("Failed to publish metric to Kafka: {}", metric.getName(), e);
            // In production, implement retry logic or dead letter queue
        }
    }
}
