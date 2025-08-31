package com.suyash.se.crawler.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.max-size:10485760}")  // Default 10MB
    private String maxSize;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * High-performance producer factory with optimized settings
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Basic Configuration
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, applicationName + "-producer");
        
        // JSON Serializer Configuration
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        
        // Performance Optimization
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);  // 32KB batches
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);     // Wait 10ms for batching
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864); // 64MB buffer
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        
        // Reliability Settings
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");       // Wait for all replicas (required for idempotence)
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);        // Retry failed sends
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15000);
        
        // Message Size
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxSize);
        
        // Idempotence for exactly-once semantics
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Kafka template with error handling
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());
        
        return template;
    }

    /**
     * Kafka Admin for topic management
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * Auto-create required topics
     */
    @Bean
    public NewTopic crawledPagesTopic() {
        return new NewTopic("crawled-pages", 3, (short) 1);
    }

    @Bean
    public NewTopic crawledPagesBatchTopic() {
        return new NewTopic("crawled-pages-batch", 3, (short) 1);
    }

    @Bean
    public NewTopic crawlingEventsTopic() {
        return new NewTopic("crawling-events", 3, (short) 1);
    }
}
