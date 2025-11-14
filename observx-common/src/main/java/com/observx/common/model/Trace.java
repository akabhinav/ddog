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
 * Distributed trace model representing a complete trace with multiple spans.
 * Compatible with OpenTelemetry trace model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trace {

    /**
     * Unique trace identifier
     */
    private String traceId;

    /**
     * Root span ID
     */
    private String rootSpanId;

    /**
     * Service name
     */
    private String serviceName;

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
     * Total duration in nanoseconds
     */
    private long durationNanos;

    /**
     * All spans in this trace
     */
    private List<Span> spans;

    /**
     * Resource attributes (service metadata)
     */
    private Map<String, String> resourceAttributes;

    /**
     * Overall trace status
     */
    private SpanStatus status;
}
