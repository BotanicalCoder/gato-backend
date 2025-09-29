package com.example.gato.api.me.dto;

import java.util.List;

public record UserProfileDto(
        String email,
        String displayName,
        int points,
        int streakCount,
        List<String> badges) {
}
