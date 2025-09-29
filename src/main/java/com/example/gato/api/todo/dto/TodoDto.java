package com.example.gato.api.todo.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TodoDto(
        UUID id,
        String title,
        String notes,
        LocalDate dueOn,
        boolean done,
        Instant doneAt) {
}
