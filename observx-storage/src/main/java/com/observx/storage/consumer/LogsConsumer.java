package com.observx.storage.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.observx.common.model.LogEntry;
import com.observx.storage.entity.LogEntity;
import com.observx.storage.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Kafka consumer for logs with batch processing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LogsConsumer {

    private final LogRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "observx.logs",
            groupId = "observx-storage",
            concurrency = "3",
            batch = "true"
    )
    public void consumeLogs(List<LogEntry> logs) {
        try {
            List<LogEntity> entities = new ArrayList<>();

            for (LogEntry log : logs) {
                LogEntity entity = LogEntity.builder()
                        .id(log.getId())
                        .timestamp(log.getTimestamp())
                        .level(log.getLevel())
                        .message(log.getMessage())
                        .service(log.getService())
                        .host(log.getHost())
                        .logger(log.getLogger())
                        .thread(log.getThread())
                        .traceId(log.getTraceId())
                        .spanId(log.getSpanId())
                        .tags(objectMapper.writeValueAsString(log.getTags()))
                        .fields(objectMapper.writeValueAsString(log.getFields()))
                        .stackTrace(log.getStackTrace())
                        .build();

                entities.add(entity);
            }

            repository.saveAll(entities);
            log.debug("Stored {} logs", entities.size());

        } catch (Exception e) {
            log.error("Error storing logs", e);
        }
    }
}
