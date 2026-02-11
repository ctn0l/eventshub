package com.example.eventshub.service;

import com.example.eventshub.dto.EventRequest;
import com.example.eventshub.dto.EventResponse;
import com.example.eventshub.messaging.EventCreatedMessage;
import com.example.eventshub.messaging.EventProducer;
import com.example.eventshub.model.Event;
import com.example.eventshub.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.NoSuchElementException;

@Service
@Validated
public class EventService {

    private final EventRepository repo;
    private final EventProducer producer;

    public EventService(EventRepository repo, EventProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    /**
     * Creates a new event after validating business rules.
     * @param request payload with event fields
     * @return created event as DTO
     * @throws IllegalArgumentException if dates are inconsistent
     */
    public EventResponse create(EventRequest request) {
        validateDates(request);
        Event entity = toEntity(request);
        Event saved = repo.save(entity);
        producer.sendEventCreated(new EventCreatedMessage(
                saved.getId(), saved.getTitle(), saved.getDescription(),
                saved.getStartsAt(), saved.getEndsAt(), saved.getLocation()
        ));
        return toResponse(saved);
    }

    /**
     * Returns a page of events filtered by optional title.
     * @param title optional search term
     * @param pageable pagination settings
     * @return page of EventResponse
     */
    public Page<EventResponse> list(String title, Pageable pageable) {
        Page<Event> page = (title == null || title.isBlank())
                ? repo.findAll(pageable)
                : repo.findByTitleContainingIgnoreCase(title, pageable);
        return page.map(this::toResponse);
    }

    /**
     * Updates an existing event.
     * @param id event id
     * @param request payload
     * @return updated DTO
     * @throws java.util.NoSuchElementException if not found
     * @throws IllegalArgumentException if dates invalid
     */
    public EventResponse update(Long id, EventRequest request) {
        validateDates(request);
        Event existing = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Event not found"));
        existing.setTitle(request.title());
        existing.setDescription(request.description());
        existing.setStartsAt(request.startsAt());
        existing.setEndsAt(request.endsAt());
        existing.setLocation(request.location());
        Event saved = repo.save(existing);
        return toResponse(saved);
    }

    /**
     * Deletes an event by id.
     * @param id event id
     */
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Event not found");
        }
        repo.deleteById(id);
    }

    private void validateDates(EventRequest request) {
        // Verifica coerenza date: end >= start se presente
        if (request.endsAt() != null && request.endsAt().isBefore(request.startsAt())) {
            throw new IllegalArgumentException("endsAt must be after startsAt");
        }
    }

    private Event toEntity(EventRequest request) {
        Event e = new Event();
        e.setTitle(request.title());
        e.setDescription(request.description());
        e.setStartsAt(request.startsAt());
        e.setEndsAt(request.endsAt());
        e.setLocation(request.location());
        return e;
    }

    private EventResponse toResponse(Event e) {
        return new EventResponse(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getStartsAt(),
                e.getEndsAt(),
                e.getLocation(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

}
