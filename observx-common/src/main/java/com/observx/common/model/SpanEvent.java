package com.observx.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Event that occurred during a span (e.g., exception, log message).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpanEvent {

    /**
     * Event name
     */
    private String name;

    /**
     * Event timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant timestamp;

    /**
     * Event attributes
     */
    private Map<String, Object> attributes;
}
