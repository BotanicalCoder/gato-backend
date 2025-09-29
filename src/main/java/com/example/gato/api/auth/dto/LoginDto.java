package com.example.gato.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @Email String email,
        @NotBlank String password) {
}
