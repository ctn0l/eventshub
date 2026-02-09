package com.example.eventshub.controller;

import com.example.eventshub.dto.EventRequest;
import com.example.eventshub.dto.EventResponse;
import com.example.eventshub.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) { this.service = service; }

    /**
     * Creates an event.
     * @param request validated payload
     * @return created event
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@RequestBody @Valid EventRequest request) {
        return service.create(request);
    }

    /**
     * Lists events with optional title filter.
     * @param title optional search term
     * @param pageable pagination and sorting
     * @return paged events
     */
    @GetMapping
    public Page<EventResponse> list(@RequestParam(required = false) String title, Pageable pageable) {
        return service.list(title, pageable);
    }

    /**
     * Updates an event.
     * @param id event id
     * @param request payload
     * @return updated event
     */
    @PutMapping("/{id}")
    public EventResponse update(@PathVariable Long id, @RequestBody @Valid EventRequest request) {
        return service.update(id, request);
    }

    /**
     * Deletes an event.
     * @param id event id
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
