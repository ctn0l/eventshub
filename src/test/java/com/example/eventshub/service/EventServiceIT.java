package com.example.eventshub.service;

import com.example.eventshub.dto.EventRequest;
import com.example.eventshub.dto.EventResponse;
import com.example.eventshub.model.Event;
import com.example.eventshub.repository.EventRepository;
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
 * Integration tests for EventService CRUD flows.
 * Note: uses @DirtiesContext to keep isolation across tests.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class EventServiceIT {

    @Autowired
    private EventService service;

    @Autowired
    private EventRepository repo;

    private Instant now;

    @BeforeEach
    void init() {
        // Inizializza timestamp di base per i test
        now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @Test
    void create_shouldPersistAndReturnDto() {
        EventRequest req = new EventRequest("Title", "Desc", now, now.plus(1, ChronoUnit.HOURS), "Online");

        EventResponse created = service.create(req);

        // Verifica che sia stato creato in DB
        assertThat(created.id()).isNotNull();
        assertThat(created.title()).isEqualTo("Title");
        assertThat(repo.count()).isEqualTo(1);
    }

    @Test
    void list_shouldFilterByTitleContainingIgnoreCase() {
        newEvent("Spring One", now, now.plus(1, ChronoUnit.HOURS), "loc");
        newEvent("Another conf", now, now.plus(2, ChronoUnit.HOURS), "loc");

        // Filtra per "spring"
        Page<EventResponse> page = service.list("spring", PageRequest.of(0, 10));

        // Verifica solo uno e case-insensitive
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().title()).isEqualTo("Spring One");
    }

    @Test
    void update_shouldApplyChanges() {
        Event saved = newEvent("Old", now, now.plus(1, ChronoUnit.HOURS), "loc");

        EventRequest req = new EventRequest("New", "nd", now.plus(1, ChronoUnit.HOURS), now.plus(2, ChronoUnit.HOURS), "Remote");
        EventResponse updated = service.update(saved.getId(), req);

        // Verifica
        assertThat(updated.title()).isEqualTo("New");
        assertThat(updated.startsAt()).isEqualTo(now.plus(1, ChronoUnit.HOURS));
        assertThat(updated.location()).isEqualTo("Remote");
    }

    @Test
    void delete_shouldRemove() {
        Event saved = newEvent("Del", now, now.plus(1, ChronoUnit.HOURS), "loc");

        service.delete(saved.getId());

        // Verifica rimozione
        assertThat(repo.existsById(saved.getId())).isFalse();
    }

    @Test
    void delete_shouldThrowWhenMissing() {
        // Tenta delete inesistente
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void create_shouldRejectInvalidDates() {
        // endsAt prima di startsAt → errore
        EventRequest bad = new EventRequest("T", null, now, now.minus(1, ChronoUnit.MINUTES), "loc");

        assertThatThrownBy(() -> service.create(bad))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Event newEvent(String title, Instant start, Instant end, String loc) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription("d");
        e.setStartsAt(start);
        e.setEndsAt(end);
        e.setLocation(loc);
        return repo.save(e);
    }

}