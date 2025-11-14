package com.observx.collector.service;

import com.observx.common.model.Trace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for ingesting distributed traces.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TracesIngestionService {

    private static final String TRACES_TOPIC = "observx.traces";

    private final KafkaTemplate<String, Trace> kafkaTemplate;

    /**
     * Ingest a single trace asynchronously
     */
    @Async
    public void ingestTrace(Trace trace) {
        publishToKafka(trace);
        log.debug("Trace ingested: {}", trace.getTraceId());
    }

    /**
     * Ingest multiple traces in parallel
     */
    public void ingestTracesBatch(List<Trace> traces) {
        traces.parallelStream().forEach(this::publishToKafka);
        log.info("Batch of {} traces ingested", traces.size());
    }

    /**
     * Publish trace to Kafka
     */
    private void publishToKafka(Trace trace) {
        try {
            kafkaTemplate.send(TRACES_TOPIC, trace.getTraceId(), trace);
        } catch (Exception e) {
            log.error("Failed to publish trace to Kafka: {}", trace.getTraceId(), e);
        }
    }
}
