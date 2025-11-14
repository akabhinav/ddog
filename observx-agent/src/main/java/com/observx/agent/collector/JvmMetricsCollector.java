package com.observx.agent.collector;

import com.observx.agent.service.MetricsPublisher;
import com.observx.common.model.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Collects JVM metrics (heap, threads, GC).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JvmMetricsCollector {

    private final MetricsPublisher publisher;

    @Value("${agent.hostname:localhost}")
    private String hostname;

    @Scheduled(fixedDelayString = "${agent.collection.interval:10000}")
    public void collectJvmMetrics() {
        log.debug("Collecting JVM metrics");

        try {
            collectMemoryMetrics();
            collectThreadMetrics();
            collectGcMetrics();
        } catch (Exception e) {
            log.error("Error collecting JVM metrics", e);
        }
    }

    private void collectMemoryMetrics() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        Map<String, String> tags = new HashMap<>();
        tags.put("host", hostname);
        tags.put("metric_type", "jvm");

        // Heap used
        publisher.publishMetric(Metric.builder()
                .name("jvm.memory.heap.used")
                .type(Metric.MetricType.GAUGE)
                .value(heapUsage.getUsed())
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("bytes")
                .build());

        // Heap max
        publisher.publishMetric(Metric.builder()
                .name("jvm.memory.heap.max")
                .type(Metric.MetricType.GAUGE)
                .value(heapUsage.getMax())
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("bytes")
                .build());

        // Non-heap used
        publisher.publishMetric(Metric.builder()
                .name("jvm.memory.nonheap.used")
                .type(Metric.MetricType.GAUGE)
                .value(nonHeapUsage.getUsed())
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("bytes")
                .build());
    }

    private void collectThreadMetrics() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        Map<String, String> tags = new HashMap<>();
        tags.put("host", hostname);
        tags.put("metric_type", "jvm");

        // Thread count
        publisher.publishMetric(Metric.builder()
                .name("jvm.threads.count")
                .type(Metric.MetricType.GAUGE)
                .value(threadBean.getThreadCount())
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("count")
                .build());

        // Peak thread count
        publisher.publishMetric(Metric.builder()
                .name("jvm.threads.peak")
                .type(Metric.MetricType.GAUGE)
                .value(threadBean.getPeakThreadCount())
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("count")
                .build());
    }

    private void collectGcMetrics() {
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            Map<String, String> tags = new HashMap<>();
            tags.put("host", hostname);
            tags.put("gc", gcBean.getName());
            tags.put("metric_type", "jvm");

            // GC count
            publisher.publishMetric(Metric.builder()
                    .name("jvm.gc.count")
                    .type(Metric.MetricType.COUNTER)
                    .value(gcBean.getCollectionCount())
                    .timestamp(Instant.now())
                    .tags(tags)
                    .source(hostname)
                    .unit("count")
                    .build());

            // GC time
            publisher.publishMetric(Metric.builder()
                    .name("jvm.gc.time")
                    .type(Metric.MetricType.COUNTER)
                    .value(gcBean.getCollectionTime())
                    .timestamp(Instant.now())
                    .tags(tags)
                    .source(hostname)
                    .unit("milliseconds")
                    .build());
        }
    }
}
