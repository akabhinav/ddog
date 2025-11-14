package com.observx.storage.entity;

import com.observx.common.model.LogEntry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA entity for storing log entries.
 */
@Entity
@Table(name = "logs", indexes = {
        @Index(name = "idx_logs_timestamp", columnList = "timestamp"),
        @Index(name = "idx_logs_service", columnList = "service"),
        @Index(name = "idx_logs_trace_id", columnList = "traceId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogEntry.LogLevel level;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    private String service;

    private String host;

    private String logger;

    private String thread;

    private String traceId;

    private String spanId;

    @Column(columnDefinition = "jsonb")
    private String tags;

    @Column(columnDefinition = "jsonb")
    private String fields;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;
}
