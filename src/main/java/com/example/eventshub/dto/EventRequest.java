package com.example.eventshub.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventRequest(
        @NotBlank String title,
        String description,
        @NotNull @FutureOrPresent Instant startsAt,
        @FutureOrPresent Instant endsAt,
        String location
) {
}
