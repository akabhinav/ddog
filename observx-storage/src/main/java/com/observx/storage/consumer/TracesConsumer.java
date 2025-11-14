package com.observx.storage.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.observx.common.model.Trace;
import com.observx.storage.entity.SpanEntity;
import com.observx.storage.repository.SpanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Kafka consumer for traces with batch processing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TracesConsumer {

    private final SpanRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "observx.traces",
            groupId = "observx-storage",
            concurrency = "3",
            batch = "true"
    )
    public void consumeTraces(List<Trace> traces) {
        try {
            List<SpanEntity> entities = new ArrayList<>();

            for (Trace trace : traces) {
                if (trace.getSpans() != null) {
                    for (var span : trace.getSpans()) {
                        SpanEntity entity = SpanEntity.builder()
                                .spanId(span.getSpanId())
                                .traceId(span.getTraceId())
                                .parentSpanId(span.getParentSpanId())
                                .name(span.getName())
                                .kind(span.getKind())
                                .startTime(span.getStartTime())
                                .endTime(span.getEndTime())
                                .durationNanos(span.getDurationNanos())
                                .serviceName(span.getServiceName())
                                .attributes(objectMapper.writeValueAsString(span.getAttributes()))
                                .events(objectMapper.writeValueAsString(span.getEvents()))
                                .status(objectMapper.writeValueAsString(span.getStatus()))
                                .links(objectMapper.writeValueAsString(span.getLinks()))
                                .build();

                        entities.add(entity);
                    }
                }
            }

            repository.saveAll(entities);
            log.debug("Stored {} spans", entities.size());

        } catch (Exception e) {
            log.error("Error storing traces", e);
        }
    }
}
