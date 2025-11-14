package com.observx.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Link from one span to another (e.g., batch processing).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpanLink {

    /**
     * Linked trace ID
     */
    private String traceId;

    /**
     * Linked span ID
     */
    private String spanId;

    /**
     * Link attributes
     */
    private Map<String, Object> attributes;
}
