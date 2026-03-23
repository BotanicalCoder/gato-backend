package com.example.gato.service;

import com.example.gato.domain.password.PasswordResetToken;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private final AppUserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final EmailService emailService;
    private final PasswordResetEmailTemplateService passwordResetEmailTemplateService;
    private final PasswordEncoder encoder;

    @Value("${app.passwordReset.tokenTtlMinutes}")
    private long tokenTtlMinutes;

    public void requestReset(String email) {
        users.findByEmail(email).ifPresent(user -> {
            PasswordResetToken token = PasswordResetToken.builder()
                    .id(UUID.randomUUID())
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiresAt(Instant.now().plus(Duration.ofMinutes(tokenTtlMinutes)))
                    .used(false)
                    .build();

            String emailBody = passwordResetEmailTemplateService.render(
                    user.getDisplayName(),
                    token.getToken(),
                    tokenTtlMinutes
            );

            tokens.save(token);
            emailService.send(
                    user.getEmail(),
                    "Reset your password",
                    emailBody,
                    true);
        });
    }

    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetToken token = tokens.findByToken(tokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalid or expired");
        }

        var user = token.getUser();
        user.setPasswordHash(encoder.encode(newPassword));
        token.setUsed(true);

        tokens.save(token);
        users.save(user);
    }
}
