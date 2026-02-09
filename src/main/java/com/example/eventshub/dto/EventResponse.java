package com.example.eventshub.dto;

import java.time.Instant;

public record EventResponse(
        Long id,
        String title,
        String description,
        Instant startsAt,
        Instant endsAt,
        String location,
        Instant createdAt,
        Instant updatedAt
) {
}
