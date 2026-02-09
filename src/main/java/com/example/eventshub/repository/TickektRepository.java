package com.example.eventshub.repository;

import com.example.eventshub.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TickektRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByEventId(Long eventId, Pageable pageable);

}
