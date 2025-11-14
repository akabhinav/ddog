package com.observx.query.controller;

import com.observx.common.dto.LogQueryRequest;
import com.observx.query.service.LogsQueryService;
import com.observx.storage.entity.LogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for querying logs.
 */
@RestController
@RequestMapping("/api/v1/query/logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LogsQueryController {

    private final LogsQueryService queryService;

    /**
     * Query logs with filters
     */
    @PostMapping
    public ResponseEntity<Page<LogEntity>> queryLogs(@RequestBody LogQueryRequest request) {
        Page<LogEntity> logs = queryService.queryLogs(request);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs by trace ID
     */
    @GetMapping("/trace/{traceId}")
    public ResponseEntity<List<LogEntity>> getLogsByTraceId(@PathVariable String traceId) {
        List<LogEntity> logs = queryService.getLogsByTraceId(traceId);
        return ResponseEntity.ok(logs);
    }

    /**
     * Search logs with full-text search
     */
    @GetMapping("/search")
    public ResponseEntity<Page<LogEntity>> searchLogs(
            @RequestParam String query,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        Page<LogEntity> logs = queryService.searchLogs(query, startTime, endTime, page, size);
        return ResponseEntity.ok(logs);
    }
}
