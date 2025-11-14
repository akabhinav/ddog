package com.observx.query.service;

import com.observx.common.dto.LogQueryRequest;
import com.observx.storage.entity.LogEntity;
import com.observx.storage.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service for querying logs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogsQueryService {

    private final LogRepository repository;

    public Page<LogEntity> queryLogs(LogQueryRequest request) {
        Instant startTime = request.getStartTime() != null ? request.getStartTime() : Instant.now().minusSeconds(3600);
        Instant endTime = request.getEndTime() != null ? request.getEndTime() : Instant.now();

        int page = request.getOffset() != null ? request.getOffset() / (request.getLimit() != null ? request.getLimit() : 100) : 0;
        int size = request.getLimit() != null ? request.getLimit() : 100;

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        if (request.getService() != null) {
            return repository.findByServiceAndTimestampBetween(request.getService(), startTime, endTime, pageable);
        } else if (request.getLevel() != null) {
            return repository.findByLevelAndTimestampBetween(request.getLevel(), startTime, endTime, pageable);
        } else if (request.getQuery() != null) {
            return repository.searchLogs(request.getQuery(), startTime, endTime, pageable);
        }

        return Page.empty(pageable);
    }

    public List<LogEntity> getLogsByTraceId(String traceId) {
        return repository.findByTraceId(traceId);
    }

    public Page<LogEntity> searchLogs(String query, Long startTimeEpoch, Long endTimeEpoch, int page, int size) {
        Instant startTime = startTimeEpoch != null ? Instant.ofEpochMilli(startTimeEpoch) : Instant.now().minusSeconds(3600);
        Instant endTime = endTimeEpoch != null ? Instant.ofEpochMilli(endTimeEpoch) : Instant.now();

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return repository.searchLogs(query, startTime, endTime, pageable);
    }
}
