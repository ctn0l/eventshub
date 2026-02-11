package com.example.eventshub.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    private final KafkaTemplate<String, EventCreatedMessage> template;
    private final String topic;

    public EventProducer(KafkaTemplate<String, EventCreatedMessage> template,
                         @Value("${app.kafka.topics.event-created}") String topic) {
        this.template = template;
        this.topic = topic;
    }

    public void sendEventCreated(EventCreatedMessage msg) {
        template.send(topic, msg.id() != null ? msg.id().toString() : null, msg);
    }
}
