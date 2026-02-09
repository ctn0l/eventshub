package com.example.eventshub.controller;

import com.example.eventshub.dto.TicketRequest;
import com.example.eventshub.dto.TicketResponse;
import com.example.eventshub.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) { this.service = service; }

    /**
     * Creates a ticket for an event.
     * @param request validated payload
     * @return created ticket DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@RequestBody @Valid TicketRequest request) {
        return service.create(request);
    }

    /**
     * Lists tickets, optionally filtered by event.
     * @param eventId optional event filter
     * @param pageable pagination
     * @return paged tickets
     */
    @GetMapping
    public Page<TicketResponse> list(@RequestParam(required = false) Long eventId, Pageable pageable) {
        return service.list(eventId, pageable);
    }

    /**
     * Updates a ticket.
     * @param id ticket id
     * @param request payload
     * @return updated ticket DTO
     */
    @PutMapping("/{id}")
    public TicketResponse update(@PathVariable Long id, @RequestBody @Valid TicketRequest request) {
        return service.update(id, request);
    }

    /**
     * Deletes a ticket.
     * @param id ticket id
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}