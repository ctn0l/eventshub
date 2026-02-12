package com.example.eventshub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Represents a notification generated from an event creation.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Notification(Long eventId, String title, String message, Instant now) {
        this.eventId = eventId;
        this.title = title;
        this.message = message;
        this.createdAt = now;
    }
}
