package com.observx.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Span model representing a single operation within a distributed trace.
 * OpenTelemetry compatible.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Span {

    /**
     * Trace ID this span belongs to
     */
    private String traceId;

    /**
     * Unique span identifier
     */
    private String spanId;

    /**
     * Parent span ID (null for root span)
     */
    private String parentSpanId;

    /**
     * Span name/operation name
     */
    private String name;

    /**
     * Span kind
     */
    private SpanKind kind;

    /**
     * Start timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant startTime;

    /**
     * End timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant endTime;

    /**
     * Duration in nanoseconds
     */
    private long durationNanos;

    /**
     * Service name
     */
    private String serviceName;

    /**
     * Span attributes (tags)
     */
    private Map<String, Object> attributes;

    /**
     * Span events (logs within the span)
     */
    private List<SpanEvent> events;

    /**
     * Span status
     */
    private SpanStatus status;

    /**
     * Links to other spans
     */
    private List<SpanLink> links;

    /**
     * Span kind enumeration
     */
    public enum SpanKind {
        /** Internal operation */
        INTERNAL,

        /** Synchronous outgoing request */
        CLIENT,

        /** Synchronous incoming request */
        SERVER,

        /** Asynchronous outgoing message */
        PRODUCER,

        /** Asynchronous incoming message */
        CONSUMER
    }
}
