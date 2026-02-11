package com.example.eventshub.messaging;

import com.example.eventshub.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumes EventCreatedMessage and generates a Notification.
 */
@Component
public class EventCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(EventCreatedListener.class);

    private final NotificationService notificationService;

    public EventCreatedListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.event-created}",
            containerFactory = "eventKafkaListenerContainerFactory"
    )
    public void handleEventCreated(@Payload EventCreatedMessage message) {
        log.info("Received EventCreatedMessage id={} title={}", message.id(), message.title());

        String notificationTitle = "New event: " + message.title();
        String notificationBody = "Starts at " + message.startsAt() + " in " + message.location();

        notificationService.createFromEvent(
                message.id(),
                notificationTitle,
                notificationBody
        );
    }
}