package com.observx.query.controller;

import com.observx.query.service.TracesQueryService;
import com.observx.storage.entity.SpanEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for querying distributed traces.
 */
@RestController
@RequestMapping("/api/v1/query/traces")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TracesQueryController {

    private final TracesQueryService queryService;

    /**
     * Get all spans for a trace ID
     */
    @GetMapping("/{traceId}")
    public ResponseEntity<List<SpanEntity>> getTrace(@PathVariable String traceId) {
        List<SpanEntity> spans = queryService.getTraceSpans(traceId);
        return ResponseEntity.ok(spans);
    }

    /**
     * Get traces by service name
     */
    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<SpanEntity>> getTracesByService(
            @PathVariable String serviceName,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime
    ) {
        List<SpanEntity> spans = queryService.getSpansByService(serviceName, startTime, endTime);
        return ResponseEntity.ok(spans);
    }
}
