package com.observx.storage.entity;

import com.observx.common.model.Span;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA entity for storing spans (distributed tracing).
 */
@Entity
@Table(name = "spans", indexes = {
        @Index(name = "idx_spans_trace_id", columnList = "traceId"),
        @Index(name = "idx_spans_start_time", columnList = "startTime")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpanEntity {

    @Id
    private String spanId;

    @Column(nullable = false)
    private String traceId;

    private String parentSpanId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Span.SpanKind kind;

    @Column(nullable = false)
    private Instant startTime;

    private Instant endTime;

    private long durationNanos;

    private String serviceName;

    @Column(columnDefinition = "jsonb")
    private String attributes;

    @Column(columnDefinition = "jsonb")
    private String events;

    @Column(columnDefinition = "jsonb")
    private String status;

    @Column(columnDefinition = "jsonb")
    private String links;
}
