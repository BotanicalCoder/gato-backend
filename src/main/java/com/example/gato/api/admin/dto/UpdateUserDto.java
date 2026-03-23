package com.example.gato.api.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateUserDto(
        @NotBlank String displayName,
        @PositiveOrZero int points,
        @PositiveOrZero int streakCount) {
}
