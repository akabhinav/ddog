package com.observx.storage.entity;

import com.observx.common.model.Metric;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA entity for storing metrics in TimescaleDB.
 * Uses TimescaleDB hypertable for efficient time-series queries.
 */
@Entity
@Table(name = "metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Metric.MetricType type;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(columnDefinition = "jsonb")
    private String tags;

    private String source;

    private String unit;

    @Column(columnDefinition = "jsonb")
    private String metadata;
}
