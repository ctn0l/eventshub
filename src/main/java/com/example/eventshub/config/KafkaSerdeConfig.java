package com.example.eventshub.config;

import com.example.eventshub.messaging.EventCreatedMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

/**
 * Provides shared JacksonJsonSerde beans for messaging.
 */
@Configuration
public class KafkaSerdeConfig {

    /**
     * Shared serde for EventCreatedMessage used by producer and consumer.
     * @return JacksonJsonSerde configured to ignore type headers on deserialization
     * Note: producer type headers are already disabled via properties.
     */
    @Bean
    public JacksonJsonSerde<EventCreatedMessage> eventCreatedSerde() {
        JacksonJsonSerde<EventCreatedMessage> serde = new JacksonJsonSerde<>(EventCreatedMessage.class);
        return serde;
    }
}