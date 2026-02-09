package com.example.eventshub.dto;

import com.example.eventshub.model.enums.TicketStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TicketRequest(
        @NotNull Long eventId,
        Long userId,
        @NotNull @Min(0) Integer priceCents,
        @NotNull TicketStatus status
) {
}
