package com.example.eventshub.config;

import com.example.eventshub.messaging.EventCreatedMessage;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, EventCreatedMessage> eventProducerFactory(
            KafkaProperties kafkaProperties,
            JacksonJsonSerde<EventCreatedMessage> eventCreatedSerde) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                eventCreatedSerde.serializer());
    }

    @Bean
    public KafkaTemplate<String, EventCreatedMessage> eventKafkaTemplate(
            ProducerFactory<String, EventCreatedMessage> eventProducerFactory) {
        return new KafkaTemplate<>(eventProducerFactory);
    }
    
}
