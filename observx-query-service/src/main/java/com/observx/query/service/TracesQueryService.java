package com.observx.query.service;

import com.observx.storage.entity.SpanEntity;
import com.observx.storage.repository.SpanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service for querying distributed traces.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TracesQueryService {

    private final SpanRepository repository;

    public List<SpanEntity> getTraceSpans(String traceId) {
        return repository.findByTraceId(traceId);
    }

    public List<SpanEntity> getSpansByService(String serviceName, Long startTimeEpoch, Long endTimeEpoch) {
        Instant startTime = startTimeEpoch != null ? Instant.ofEpochMilli(startTimeEpoch) : Instant.now().minusSeconds(3600);
        Instant endTime = endTimeEpoch != null ? Instant.ofEpochMilli(endTimeEpoch) : Instant.now();

        return repository.findByServiceNameAndStartTimeBetween(serviceName, startTime, endTime);
    }
}
