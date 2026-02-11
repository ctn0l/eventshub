package com.example.eventshub.repository;

import com.example.eventshub.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CRUD access to notifications generated from events.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
