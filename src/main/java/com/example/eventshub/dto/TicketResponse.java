package com.example.eventshub.dto;

import com.example.eventshub.model.enums.TicketStatus;

import java.time.Instant;

public record TicketResponse(
        Long id,
        Long eventId,
        Long userId,
        Integer priceCents,
        TicketStatus status,
        Instant purchasedAt
) {
}
