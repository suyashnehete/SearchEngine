package com.suyash.se.crawler.monitoring;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Health indicator for Kafka connectivity and performance
 */
@Component
@Slf4j
public class KafkaHealthIndicator implements HealthIndicator {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Health health() {
        // Get bootstrap servers from KafkaTemplate
        String bootstrapServers = kafkaTemplate.getProducerFactory()
                .getConfigurationProperties()
                .getOrDefault(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "")
                .toString();

        try (AdminClient adminClient = AdminClient.create(
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {

            DescribeClusterResult clusterResult = adminClient.describeCluster();
            String clusterId = clusterResult.clusterId().get(5, TimeUnit.SECONDS);
            int nodeCount = clusterResult.nodes().get(5, TimeUnit.SECONDS).size();

            // Get producer metrics
            Map<org.apache.kafka.common.MetricName, ? extends org.apache.kafka.common.Metric> metrics =
                    kafkaTemplate.metrics();

            return Health.up()
                    .withDetail("clusterId", clusterId)
                    .withDetail("nodeCount", nodeCount)
                    .withDetail("producerMetricsCount", metrics.size())
                    .withDetail("bootstrapServers", bootstrapServers)
                    .build();

        } catch (TimeoutException e) {
            log.error("Kafka health check timed out", e);
            return Health.down()
                    .withDetail("error", "Timeout while connecting to Kafka")
                    .withException(e)
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Kafka health check interrupted", e);
            return Health.down()
                    .withDetail("error", "Interrupted while connecting to Kafka")
                    .withException(e)
                    .build();
        } catch (ExecutionException e) {
            log.error("Kafka health check execution error", e);
            return Health.down()
                    .withDetail("error", "Execution error while connecting to Kafka")
                    .withException(e)
                    .build();
        } catch (Exception e) {
            log.error("Kafka health check unexpected error", e);
            return Health.down()
                    .withDetail("error", "Unexpected error while connecting to Kafka")
                    .withException(e)
                    .build();
        }
    }
}