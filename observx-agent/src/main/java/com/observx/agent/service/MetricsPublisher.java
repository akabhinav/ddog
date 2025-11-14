package com.observx.agent.service;

import com.observx.common.model.Metric;
import com.observx.common.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Publishes collected metrics to the collector service.
 */
@Service
@Slf4j
public class MetricsPublisher {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${collector.url:http://localhost:8081}")
    private String collectorUrl;

    public void publishMetric(Metric metric) {
        try {
            if (metric.getId() == null) {
                metric.setId(IdGenerator.generateUuid());
            }

            String url = collectorUrl + "/api/v1/metrics";
            restTemplate.postForEntity(url, metric, String.class);

            log.trace("Published metric: {} = {}", metric.getName(), metric.getValue());
        } catch (Exception e) {
            log.error("Failed to publish metric: {}", metric.getName(), e);
        }
    }
}
