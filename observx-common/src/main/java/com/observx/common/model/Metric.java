package com.observx.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Core metric data model representing a single metric data point.
 * Supports various metric types: counter, gauge, histogram, summary.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metric {

    /**
     * Unique identifier for the metric
     */
    private String id;

    /**
     * Metric name (e.g., "http.requests.total", "cpu.usage.percent")
     */
    private String name;

    /**
     * Metric type
     */
    private MetricType type;

    /**
     * Numeric value of the metric
     */
    private double value;

    /**
     * Timestamp when the metric was recorded
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant timestamp;

    /**
     * Tags/labels for dimensional data (e.g., {"host": "server-1", "region": "us-east"})
     */
    private Map<String, String> tags;

    /**
     * Source/host that generated the metric
     */
    private String source;

    /**
     * Unit of measurement (e.g., "bytes", "milliseconds", "percent")
     */
    private String unit;

    /**
     * Additional metadata
     */
    private Map<String, Object> metadata;

    /**
     * Metric type enumeration
     */
    public enum MetricType {
        /** Counter - monotonically increasing value */
        COUNTER,

        /** Gauge - value that can go up or down */
        GAUGE,

        /** Histogram - statistical distribution */
        HISTOGRAM,

        /** Summary - similar to histogram with quantiles */
        SUMMARY,

        /** Rate - rate of change over time */
        RATE
    }
}
