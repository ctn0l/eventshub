package com.example.eventshub.service;

import com.example.eventshub.model.Notification;
import com.example.eventshub.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

@Service
@Validated
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public Notification createFromEvent(Long eventId, String title, String message) {
        if (eventId == null) {
            throw new IllegalArgumentException("eventId is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }

        Notification n = new Notification(
                eventId,
                title,
                message,
                Instant.now()
        );
        return repo.save(n);
    }

}
