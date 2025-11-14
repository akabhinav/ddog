package com.observx.storage.repository;

import com.observx.storage.entity.MetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for metrics stored in TimescaleDB.
 */
@Repository
public interface MetricRepository extends JpaRepository<MetricEntity, String> {

    /**
     * Find metrics by name and time range
     */
    List<MetricEntity> findByNameAndTimestampBetween(
            String name,
            Instant startTime,
            Instant endTime
    );

    /**
     * Find metrics by name pattern and time range
     */
    @Query("SELECT m FROM MetricEntity m WHERE m.name LIKE :pattern AND m.timestamp BETWEEN :startTime AND :endTime")
    List<MetricEntity> findByNamePatternAndTimeRange(
            @Param("pattern") String pattern,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );

    /**
     * Delete old metrics (for data retention)
     */
    void deleteByTimestampBefore(Instant cutoffTime);
}
