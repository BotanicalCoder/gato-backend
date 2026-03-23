package com.example.gato.api.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record BadgeUpsertDto(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @PositiveOrZero Integer minPoints,
        @PositiveOrZero Integer minStreakCount,
        @PositiveOrZero Integer minCompletedTodos) {
}
