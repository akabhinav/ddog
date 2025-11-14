package com.observx.collector.controller;

import com.observx.collector.service.MetricsIngestionService;
import com.observx.common.model.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for ingesting metrics.
 * Supports both single and batch metric ingestion.
 */
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Slf4j
public class MetricsCollectorController {

    private final MetricsIngestionService ingestionService;

    /**
     * Ingest a single metric
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> ingestMetric(@RequestBody Metric metric) {
        log.debug("Receiving metric: {}", metric.getName());

        ingestionService.ingestMetric(metric);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "accepted",
                        "metricId", metric.getId() != null ? metric.getId() : "generated"
                ));
    }

    /**
     * Ingest multiple metrics in a batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> ingestMetrics(@RequestBody List<Metric> metrics) {
        log.debug("Receiving batch of {} metrics", metrics.size());

        ingestionService.ingestMetricsBatch(metrics);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "accepted",
                        "count", metrics.size()
                ));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "metrics-collector"
        ));
    }
}
