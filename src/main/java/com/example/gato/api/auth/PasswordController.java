package com.example.gato.api.auth;

import com.example.gato.api.auth.dto.ForgotPasswordRequest;
import com.example.gato.api.auth.dto.ResetPasswordRequest;
import com.example.gato.domain.password.PasswordResetToken;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.PasswordResetTokenRepository;
import com.example.gato.email.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordController {

    private final AppUserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final EmailService email;
    private final PasswordEncoder encoder;

    @Value("${app.passwordReset.tokenTtlMinutes}")
    private long tokenTtlMinutes;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@Valid @RequestBody ForgotPasswordRequest req) {
        users.findByEmail(req.email()).ifPresent(u -> {
            var t = PasswordResetToken.builder()
                    .id(UUID.randomUUID())
                    .user(u)
                    .token(UUID.randomUUID().toString())
                    .expiresAt(Instant.now().plus(Duration.ofMinutes(tokenTtlMinutes)))
                    .used(false)
                    .build();
            tokens.save(t);
            email.send(u.getEmail(), "Reset your password",
                    "Use this token within " + tokenTtlMinutes + " mins: " + t.getToken());
        });
        return ResponseEntity.ok(Map.of("message", "If the email exists, a reset token was sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@Valid @RequestBody ResetPasswordRequest req) {
        var t = tokens.findByToken(req.token())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

        if (t.isUsed() || t.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalid or expired");
        }

        var u = t.getUser();
        u.setPasswordHash(encoder.encode(req.newPassword()));
        t.setUsed(true);

        tokens.save(t);
        users.save(u);

        return ResponseEntity.ok(Map.of("message", "Password reset"));
    }
}
