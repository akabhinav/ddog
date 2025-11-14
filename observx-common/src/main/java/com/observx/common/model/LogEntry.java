package com.observx.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Log entry model representing a single log event.
 * Supports structured logging with tags and metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {

    /**
     * Unique identifier for the log entry
     */
    private String id;

    /**
     * Timestamp when the log was generated
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant timestamp;

    /**
     * Log level
     */
    private LogLevel level;

    /**
     * Log message
     */
    private String message;

    /**
     * Source service/application
     */
    private String service;

    /**
     * Host/source identifier
     */
    private String host;

    /**
     * Logger name (e.g., class name)
     */
    private String logger;

    /**
     * Thread name
     */
    private String thread;

    /**
     * Trace ID for correlation with distributed traces
     */
    private String traceId;

    /**
     * Span ID for correlation with distributed traces
     */
    private String spanId;

    /**
     * Tags/labels for filtering and grouping
     */
    private Map<String, String> tags;

    /**
     * Structured fields (key-value pairs from structured logging)
     */
    private Map<String, Object> fields;

    /**
     * Stack trace for errors/exceptions
     */
    private String stackTrace;

    /**
     * Log level enumeration
     */
    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }
}
