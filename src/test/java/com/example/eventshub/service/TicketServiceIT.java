package com.example.eventshub.service;

import com.example.eventshub.dto.TicketRequest;
import com.example.eventshub.dto.TicketResponse;
import com.example.eventshub.model.Event;
import com.example.eventshub.model.Ticket;
import com.example.eventshub.model.User;
import com.example.eventshub.model.enums.TicketStatus;
import com.example.eventshub.repository.EventRepository;
import com.example.eventshub.repository.TickektRepository;
import com.example.eventshub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for TicketService CRUD flows.
 * Note: seeds Event and User where needed.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class TicketServiceIT {

    @Autowired
    private TicketService service;

    @Autowired
    private TickektRepository ticketRepo;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private UserRepository userRepo;

    private Event event;
    private User user;

    @BeforeEach
    void setUp() {
        // Crea evento richiesto
        event = newEvent("Event", Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS), "loc");
        // Crea utente opzionale
        user = newUser("mario@example.com", "password1!");
    }

    @Test
    void create_shouldPersistWithEventAndUser() {
        // Request con user
        TicketRequest req = new TicketRequest(event.getId(), user.getId(), 1500, TicketStatus.PAID);

        TicketResponse created = service.create(req);

        assertThat(created.id()).isNotNull();
        assertThat(created.eventId()).isEqualTo(event.getId());
        assertThat(created.userId()).isEqualTo(user.getId());
        assertThat(ticketRepo.count()).isEqualTo(1);
    }

    @Test
    void list_shouldFilterByEventId() {
        // Seed due ticket per event, uno per altro event
        Event other = newEvent("Other", Instant.now(), Instant.now().plus(2, ChronoUnit.HOURS), "loc2");
        newTicket(event, user, 1000, TicketStatus.PAID);
        newTicket(event, null, 2000, TicketStatus.PENDING);
        newTicket(other, null, 3000, TicketStatus.PAID);

        Page<TicketResponse> page = service.list(event.getId(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).allMatch(tr -> tr.eventId().equals(event.getId()));
    }

    @Test
    void update_shouldChangeFields() {
        Ticket saved = newTicket(event, user, 1000, TicketStatus.PENDING);

        // Update: cambia prezzo e status, rimuove user
        TicketRequest req = new TicketRequest(event.getId(), null, 2500, TicketStatus.PAID);
        TicketResponse updated = service.update(saved.getId(), req);

        assertThat(updated.priceCents()).isEqualTo(2500);
        assertThat(updated.status()).isEqualTo(TicketStatus.PAID);
        assertThat(updated.userId()).isNull();
    }

    @Test
    void delete_shouldRemove() {
        Ticket saved = newTicket(event, user, 500, TicketStatus.PENDING);

        service.delete(saved.getId());

        assertThat(ticketRepo.existsById(saved.getId())).isFalse();
    }

    @Test
    void create_shouldFailIfEventMissing() {
        TicketRequest bad = new TicketRequest(999L, null, 100, TicketStatus.PENDING);

        assertThatThrownBy(() -> service.create(bad))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void create_shouldFailIfUserMissingWhenProvided() {
        TicketRequest bad = new TicketRequest(event.getId(), 999L, 100, TicketStatus.PENDING);

        assertThatThrownBy(() -> service.create(bad))
                .isInstanceOf(NoSuchElementException.class);
    }

    private Event newEvent(String title, Instant start, Instant end, String loc) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription("d");
        e.setStartsAt(start);
        e.setEndsAt(end);
        e.setLocation(loc);
        return eventRepo.save(e);
    }

    private User newUser(String email, String password) {
        User u = new User();
        u.setEmail(email);
        u.setPassword(password);
        return userRepo.save(u);
    }

    private Ticket newTicket(Event event, User user, int priceCents, TicketStatus status) {
        Ticket t = new Ticket();
        t.setEvent(event);
        t.setUser(user);
        t.setPriceCents(priceCents);
        t.setStatus(status);
        return ticketRepo.save(t);
    }
}