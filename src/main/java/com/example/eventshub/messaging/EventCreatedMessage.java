package com.example.eventshub.messaging;

import java.time.Instant;

public record EventCreatedMessage(Long id, String title, String description,
                                  Instant startsAt, Instant endsAt, String location) {
}
