package com.observx.agent.collector;

import com.observx.agent.service.MetricsPublisher;
import com.observx.common.model.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Collects system metrics using OSHI library.
 * Runs every 10 seconds by default.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SystemMetricsCollector {

    private final MetricsPublisher publisher;
    private final SystemInfo systemInfo = new SystemInfo();
    private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private final OperatingSystem os = systemInfo.getOperatingSystem();

    @Value("${agent.hostname:localhost}")
    private String hostname;

    @Scheduled(fixedDelayString = "${agent.collection.interval:10000}")
    public void collectMetrics() {
        log.debug("Collecting system metrics from {}", hostname);

        try {
            collectCpuMetrics();
            collectMemoryMetrics();
            collectDiskMetrics();
            collectNetworkMetrics();
        } catch (Exception e) {
            log.error("Error collecting system metrics", e);
        }
    }

    private void collectCpuMetrics() {
        CentralProcessor processor = hardware.getProcessor();
        double cpuLoad = processor.getSystemCpuLoad(1000) * 100;

        Map<String, String> tags = new HashMap<>();
        tags.put("host", hostname);
        tags.put("metric_type", "system");

        Metric metric = Metric.builder()
                .name("system.cpu.usage")
                .type(Metric.MetricType.GAUGE)
                .value(cpuLoad)
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("percent")
                .build();

        publisher.publishMetric(metric);

        // CPU count
        Metric cpuCountMetric = Metric.builder()
                .name("system.cpu.count")
                .type(Metric.MetricType.GAUGE)
                .value(processor.getLogicalProcessorCount())
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("count")
                .build();

        publisher.publishMetric(cpuCountMetric);
    }

    private void collectMemoryMetrics() {
        GlobalMemory memory = hardware.getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;

        Map<String, String> tags = new HashMap<>();
        tags.put("host", hostname);
        tags.put("metric_type", "system");

        // Memory usage percentage
        double memoryUsagePercent = (double) usedMemory / totalMemory * 100;

        Metric memoryUsageMetric = Metric.builder()
                .name("system.memory.usage")
                .type(Metric.MetricType.GAUGE)
                .value(memoryUsagePercent)
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("percent")
                .build();

        publisher.publishMetric(memoryUsageMetric);

        // Memory used in bytes
        Metric memoryUsedMetric = Metric.builder()
                .name("system.memory.used")
                .type(Metric.MetricType.GAUGE)
                .value(usedMemory)
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("bytes")
                .build();

        publisher.publishMetric(memoryUsedMetric);

        // Memory total
        Metric memoryTotalMetric = Metric.builder()
                .name("system.memory.total")
                .type(Metric.MetricType.GAUGE)
                .value(totalMemory)
                .timestamp(Instant.now())
                .tags(tags)
                .source(hostname)
                .unit("bytes")
                .build();

        publisher.publishMetric(memoryTotalMetric);
    }

    private void collectDiskMetrics() {
        for (HWDiskStore disk : hardware.getDiskStores()) {
            Map<String, String> tags = new HashMap<>();
            tags.put("host", hostname);
            tags.put("disk", disk.getName());
            tags.put("metric_type", "system");

            // Disk read bytes
            Metric diskReadMetric = Metric.builder()
                    .name("system.disk.read_bytes")
                    .type(Metric.MetricType.COUNTER)
                    .value(disk.getReadBytes())
                    .timestamp(Instant.now())
                    .tags(tags)
                    .source(hostname)
                    .unit("bytes")
                    .build();

            publisher.publishMetric(diskReadMetric);

            // Disk write bytes
            Metric diskWriteMetric = Metric.builder()
                    .name("system.disk.write_bytes")
                    .type(Metric.MetricType.COUNTER)
                    .value(disk.getWriteBytes())
                    .timestamp(Instant.now())
                    .tags(tags)
                    .source(hostname)
                    .unit("bytes")
                    .build();

            publisher.publishMetric(diskWriteMetric);
        }
    }

    private void collectNetworkMetrics() {
        for (NetworkIF net : hardware.getNetworkIFs()) {
            if (net.getBytesRecv() > 0 || net.getBytesSent() > 0) {
                Map<String, String> tags = new HashMap<>();
                tags.put("host", hostname);
                tags.put("interface", net.getName());
                tags.put("metric_type", "system");

                // Network received bytes
                Metric netRecvMetric = Metric.builder()
                        .name("system.network.bytes_recv")
                        .type(Metric.MetricType.COUNTER)
                        .value(net.getBytesRecv())
                        .timestamp(Instant.now())
                        .tags(tags)
                        .source(hostname)
                        .unit("bytes")
                        .build();

                publisher.publishMetric(netRecvMetric);

                // Network sent bytes
                Metric netSentMetric = Metric.builder()
                        .name("system.network.bytes_sent")
                        .type(Metric.MetricType.COUNTER)
                        .value(net.getBytesSent())
                        .timestamp(Instant.now())
                        .tags(tags)
                        .source(hostname)
                        .unit("bytes")
                        .build();

                publisher.publishMetric(netSentMetric);
            }
        }
    }
}
