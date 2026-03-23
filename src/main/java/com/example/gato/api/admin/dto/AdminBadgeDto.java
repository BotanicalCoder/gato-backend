package com.example.gato.api.admin.dto;

import java.util.UUID;

public record AdminBadgeDto(
        UUID id,
        String code,
        String name,
        String description,
        Integer minPoints,
        Integer minStreakCount,
        Integer minCompletedTodos) {
}
