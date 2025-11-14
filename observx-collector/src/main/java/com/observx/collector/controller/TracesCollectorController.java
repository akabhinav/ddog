package com.observx.collector.controller;

import com.observx.collector.service.TracesIngestionService;
import com.observx.common.model.Trace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for ingesting distributed traces.
 * OpenTelemetry compatible endpoint.
 */
@RestController
@RequestMapping("/api/v1/traces")
@RequiredArgsConstructor
@Slf4j
public class TracesCollectorController {

    private final TracesIngestionService ingestionService;

    /**
     * Ingest a single trace
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> ingestTrace(@RequestBody Trace trace) {
        log.debug("Receiving trace: {}", trace.getTraceId());

        ingestionService.ingestTrace(trace);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "accepted",
                        "traceId", trace.getTraceId()
                ));
    }

    /**
     * Ingest multiple traces in a batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> ingestTraces(@RequestBody List<Trace> traces) {
        log.debug("Receiving batch of {} traces", traces.size());

        ingestionService.ingestTracesBatch(traces);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "accepted",
                        "count", traces.size()
                ));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "traces-collector"
        ));
    }
}
