package com.observx.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Request DTO for querying metrics with time range and filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricQueryRequest {

    /**
     * Metric name pattern (supports wildcards)
     */
    private String metricName;

    /**
     * Start time for the query range
     */
    private Instant startTime;

    /**
     * End time for the query range
     */
    private Instant endTime;

    /**
     * Tag filters (all must match)
     */
    private Map<String, String> tags;

    /**
     * Aggregation function (avg, sum, min, max, count)
     */
    private String aggregation;

    /**
     * Group by tags
     */
    private String[] groupBy;

    /**
     * Time window for aggregation (e.g., "1m", "5m", "1h")
     */
    private String interval;

    /**
     * Maximum number of results
     */
    private Integer limit;
}
