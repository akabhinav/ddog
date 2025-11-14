package com.observx.query.controller;

import com.observx.common.dto.MetricQueryRequest;
import com.observx.query.service.MetricsQueryService;
import com.observx.storage.entity.MetricEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for querying metrics.
 */
@RestController
@RequestMapping("/api/v1/query/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetricsQueryController {

    private final MetricsQueryService queryService;

    /**
     * Query metrics with filters
     */
    @PostMapping
    public ResponseEntity<List<MetricEntity>> queryMetrics(@RequestBody MetricQueryRequest request) {
        List<MetricEntity> metrics = queryService.queryMetrics(request);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get latest value for a metric
     */
    @GetMapping("/{metricName}/latest")
    public ResponseEntity<MetricEntity> getLatestMetric(@PathVariable String metricName) {
        return queryService.getLatestMetric(metricName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get aggregated metrics
     */
    @PostMapping("/aggregate")
    public ResponseEntity<?> getAggregatedMetrics(@RequestBody MetricQueryRequest request) {
        var result = queryService.getAggregatedMetrics(request);
        return ResponseEntity.ok(result);
    }
}
