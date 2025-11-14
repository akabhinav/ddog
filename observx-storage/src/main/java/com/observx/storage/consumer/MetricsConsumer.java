package com.observx.storage.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.observx.common.model.Metric;
import com.observx.storage.entity.MetricEntity;
import com.observx.storage.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Kafka consumer for metrics with batch processing for high performance.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsConsumer {

    private final MetricRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "observx.metrics",
            groupId = "observx-storage",
            concurrency = "3",
            batch = "true"
    )
    public void consumeMetrics(List<Metric> metrics) {
        try {
            List<MetricEntity> entities = new ArrayList<>();

            for (Metric metric : metrics) {
                MetricEntity entity = MetricEntity.builder()
                        .id(metric.getId())
                        .name(metric.getName())
                        .type(metric.getType())
                        .value(metric.getValue())
                        .timestamp(metric.getTimestamp())
                        .tags(objectMapper.writeValueAsString(metric.getTags()))
                        .source(metric.getSource())
                        .unit(metric.getUnit())
                        .metadata(objectMapper.writeValueAsString(metric.getMetadata()))
                        .build();

                entities.add(entity);
            }

            repository.saveAll(entities);
            log.debug("Stored {} metrics", entities.size());

        } catch (Exception e) {
            log.error("Error storing metrics", e);
        }
    }
}
