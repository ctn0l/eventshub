package com.example.eventshub.service;

import com.example.eventshub.dto.TicketRequest;
import com.example.eventshub.dto.TicketResponse;
import com.example.eventshub.model.Event;
import com.example.eventshub.model.Ticket;
import com.example.eventshub.model.User;
import com.example.eventshub.repository.EventRepository;
import com.example.eventshub.repository.TickektRepository;
import com.example.eventshub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.NoSuchElementException;

@Service
@Validated
public class TicketService {

    private final TickektRepository repo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    public TicketService(TickektRepository repo, EventRepository eventRepo, UserRepository userRepo) {
        this.repo = repo;
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
    }

    /**
     * Creates a ticket for an event.
     * @param request payload
     * @return created ticket DTO
     * @throws java.util.NoSuchElementException if event or user (when provided) is missing
     */
    public TicketResponse create(TicketRequest request) {
        Ticket entity = toEntity(request);
        Ticket saved = repo.save(entity);
        return toResponse(saved);
    }

    /**
     * Returns a paged list of tickets, optionally filtered by event.
     * @param eventId optional event filter
     * @param pageable pagination
     * @return page of TicketResponse
     */
    public Page<TicketResponse> list(Long eventId, Pageable pageable) {
        Page<Ticket> page = (eventId == null)
                ? repo.findAll(pageable)
                : repo.findByEventId(eventId, pageable);
        return page.map(this::toResponse);
    }

    /**
     * Updates an existing ticket.
     * @param id ticket id
     * @param request payload
     * @return updated DTO
     * @throws NoSuchElementException if ticket not found
     */
    public TicketResponse update(Long id, TicketRequest request) {
        Ticket existing = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Ticket not found"));
        apply(existing, request);
        Ticket saved = repo.save(existing);
        return toResponse(saved);
    }

    /**
     * Deletes a ticket by id.
     * @param id ticket id
     */
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Ticket not found");
        }
        repo.deleteById(id);
    }

    private Ticket toEntity(TicketRequest request) {
        Ticket t = new Ticket();
        apply(t, request);
        return t;
    }

    private void apply(Ticket t, TicketRequest request) {
        // Imposta evento obbligatorio
        Event event = eventRepo.findById(request.eventId())
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
        t.setEvent(event);

        // Imposta utente se presente
        if (request.userId() != null) {
            User user = userRepo.findById(request.userId())
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            t.setUser(user);
        } else {
            t.setUser(null);
        }

        // Aggiorna campi semplici
        t.setPriceCents(request.priceCents());
        t.setStatus(request.status());
    }

    private TicketResponse toResponse(Ticket t) {
        return new TicketResponse(
                t.getId(),
                t.getEvent() != null ? t.getEvent().getId() : null,
                t.getUser() != null ? t.getUser().getId() : null,
                t.getPriceCents(),
                t.getStatus(),
                t.getPurchasedAt()
        );
    }

}
