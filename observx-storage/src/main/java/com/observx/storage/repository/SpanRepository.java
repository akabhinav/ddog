package com.observx.storage.repository;

import com.observx.storage.entity.SpanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for distributed tracing spans.
 */
@Repository
public interface SpanRepository extends JpaRepository<SpanEntity, String> {

    /**
     * Find all spans for a trace
     */
    List<SpanEntity> findByTraceId(String traceId);

    /**
     * Find spans by service and time range
     */
    List<SpanEntity> findByServiceNameAndStartTimeBetween(
            String serviceName,
            Instant startTime,
            Instant endTime
    );

    /**
     * Delete old spans (for data retention)
     */
    void deleteByStartTimeBefore(Instant cutoffTime);
}
