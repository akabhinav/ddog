package com.observx.collector.controller;

import com.observx.collector.service.LogsIngestionService;
import com.observx.common.model.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for ingesting logs.
 * Supports structured logging with tags and metadata.
 */
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
@Slf4j
public class LogsCollectorController {

    private final LogsIngestionService ingestionService;

    /**
     * Ingest a single log entry
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> ingestLog(@RequestBody LogEntry logEntry) {
        log.debug("Receiving log from service: {}", logEntry.getService());

        ingestionService.ingestLog(logEntry);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "accepted",
                        "logId", logEntry.getId() != null ? logEntry.getId() : "generated"
                ));
    }

    /**
     * Ingest multiple logs in a batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> ingestLogs(@RequestBody List<LogEntry> logs) {
        log.debug("Receiving batch of {} logs", logs.size());

        ingestionService.ingestLogsBatch(logs);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "accepted",
                        "count", logs.size()
                ));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "logs-collector"
        ));
    }
}
