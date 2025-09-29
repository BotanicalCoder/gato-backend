package com.example.gato.api.todo.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record CreateTodoDto(
        @NotBlank String title,
        String notes,
        LocalDate dueOn) {
}
