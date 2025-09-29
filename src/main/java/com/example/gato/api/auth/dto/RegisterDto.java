package com.example.gato.api.auth.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @jakarta.validation.constraints.Email String email,
        @jakarta.validation.constraints.NotBlank String displayName,

        @Size(min = 8, message = "Password must be at least 8 characters long.")

        // 1+ lowercase, 1+ uppercase, 1+ digit, 1+ special, min length 8
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$", message = "Password must contain at least 1 uppercase, 1 lowercase, 1 number, and 1 special character.") String password) {
}
