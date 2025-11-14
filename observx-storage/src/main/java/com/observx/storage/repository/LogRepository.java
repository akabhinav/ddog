package com.observx.storage.repository;

import com.observx.common.model.LogEntry;
import com.observx.storage.entity.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for log entries.
 */
@Repository
public interface LogRepository extends JpaRepository<LogEntity, String> {

    /**
     * Find logs by service and time range
     */
    Page<LogEntity> findByServiceAndTimestampBetween(
            String service,
            Instant startTime,
            Instant endTime,
            Pageable pageable
    );

    /**
     * Find logs by trace ID
     */
    List<LogEntity> findByTraceId(String traceId);

    /**
     * Find logs by level and time range
     */
    Page<LogEntity> findByLevelAndTimestampBetween(
            LogEntry.LogLevel level,
            Instant startTime,
            Instant endTime,
            Pageable pageable
    );

    /**
     * Full-text search in log messages
     */
    @Query("SELECT l FROM LogEntity l WHERE l.message LIKE %:query% AND l.timestamp BETWEEN :startTime AND :endTime")
    Page<LogEntity> searchLogs(
            @Param("query") String query,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable
    );

    /**
     * Delete old logs (for data retention)
     */
    void deleteByTimestampBefore(Instant cutoffTime);
}
