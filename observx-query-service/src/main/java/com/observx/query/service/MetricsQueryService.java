package com.observx.query.service;

import com.observx.common.dto.MetricQueryRequest;
import com.observx.storage.entity.MetricEntity;
import com.observx.storage.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * Service for querying metrics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsQueryService {

    private final MetricRepository repository;

    public List<MetricEntity> queryMetrics(MetricQueryRequest request) {
        Instant startTime = request.getStartTime() != null ? request.getStartTime() : Instant.now().minusSeconds(3600);
        Instant endTime = request.getEndTime() != null ? request.getEndTime() : Instant.now();

        if (request.getMetricName() != null && request.getMetricName().contains("*")) {
            String pattern = request.getMetricName().replace("*", "%");
            return repository.findByNamePatternAndTimeRange(pattern, startTime, endTime);
        } else if (request.getMetricName() != null) {
            return repository.findByNameAndTimestampBetween(request.getMetricName(), startTime, endTime);
        }

        return Collections.emptyList();
    }

    public Optional<MetricEntity> getLatestMetric(String metricName) {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);

        List<MetricEntity> metrics = repository.findByNameAndTimestampBetween(metricName, yesterday, now);

        return metrics.stream()
                .max(Comparator.comparing(MetricEntity::getTimestamp));
    }

    public Map<String, Object> getAggregatedMetrics(MetricQueryRequest request) {
        List<MetricEntity> metrics = queryMetrics(request);

        if (metrics.isEmpty()) {
            return Map.of("count", 0);
        }

        DoubleSummaryStatistics stats = metrics.stream()
                .mapToDouble(MetricEntity::getValue)
                .summaryStatistics();

        return Map.of(
                "count", stats.getCount(),
                "avg", stats.getAverage(),
                "min", stats.getMin(),
                "max", stats.getMax(),
                "sum", stats.getSum()
        );
    }
}
