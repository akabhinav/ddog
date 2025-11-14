package com.observx.collector.service;

import com.observx.common.model.LogEntry;
import com.observx.common.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service for ingesting logs with high performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogsIngestionService {

    private static final String LOGS_TOPIC = "observx.logs";

    private final KafkaTemplate<String, LogEntry> kafkaTemplate;

    /**
     * Ingest a single log entry asynchronously
     */
    @Async
    public void ingestLog(LogEntry logEntry) {
        enrichLogEntry(logEntry);
        publishToKafka(logEntry);
        log.debug("Log ingested from service: {}", logEntry.getService());
    }

    /**
     * Ingest multiple logs in parallel
     */
    public void ingestLogsBatch(List<LogEntry> logs) {
        logs.parallelStream().forEach(logEntry -> {
            enrichLogEntry(logEntry);
            publishToKafka(logEntry);
        });
        log.info("Batch of {} logs ingested", logs.size());
    }

    /**
     * Enrich log entry with generated ID and timestamp if missing
     */
    private void enrichLogEntry(LogEntry logEntry) {
        if (logEntry.getId() == null) {
            logEntry.setId(IdGenerator.generateUuid());
        }
        if (logEntry.getTimestamp() == null) {
            logEntry.setTimestamp(Instant.now());
        }
    }

    /**
     * Publish log to Kafka
     */
    private void publishToKafka(LogEntry logEntry) {
        try {
            String key = logEntry.getTraceId() != null ? logEntry.getTraceId() : logEntry.getService();
            kafkaTemplate.send(LOGS_TOPIC, key, logEntry);
        } catch (Exception e) {
            log.error("Failed to publish log to Kafka", e);
        }
    }
}
