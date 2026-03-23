package com.example.gato.api.admin.dto;

import java.util.List;
import java.util.UUID;

public record AdminUserDto(
        UUID id,
        String email,
        String displayName,
        boolean admin,
        int points,
        int streakCount,
        List<String> badges) {
}
