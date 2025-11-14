package com.observx.common.dto;

import com.observx.common.model.LogEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Request DTO for querying logs with filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogQueryRequest {

    /**
     * Search query string (full-text search)
     */
    private String query;

    /**
     * Start time for the query range
     */
    private Instant startTime;

    /**
     * End time for the query range
     */
    private Instant endTime;

    /**
     * Service filter
     */
    private String service;

    /**
     * Host filter
     */
    private String host;

    /**
     * Log level filter
     */
    private LogEntry.LogLevel level;

    /**
     * Trace ID filter
     */
    private String traceId;

    /**
     * Tag filters
     */
    private Map<String, String> tags;

    /**
     * Maximum number of results
     */
    private Integer limit;

    /**
     * Offset for pagination
     */
    private Integer offset;
}
