package com.example.eventshub.config;

import com.example.eventshub.messaging.EventCreatedMessage;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer configuration for EventCreatedMessage.
 */
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, EventCreatedMessage> eventConsumerFactory(
            KafkaProperties kafkaProperties,
            JacksonJsonSerde<EventCreatedMessage> eventCreatedSerde) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                eventCreatedSerde.deserializer());
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventCreatedMessage> eventKafkaListenerContainerFactory(
            ConsumerFactory<String, EventCreatedMessage> eventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, EventCreatedMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(eventConsumerFactory);
        return factory;
    }
}