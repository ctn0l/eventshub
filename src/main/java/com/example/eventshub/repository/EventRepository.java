package com.example.eventshub.repository;

import com.example.eventshub.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
