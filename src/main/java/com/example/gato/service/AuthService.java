package com.example.gato.service;

import com.example.gato.api.auth.dto.LoginDto;
import com.example.gato.api.auth.dto.RegisterDto;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public ResponseEntity<?> register(RegisterDto dto) {
        if (users.existsByEmail(dto.email())) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Email already in use", "success", false, "data", Map.of()));
        }

        LOGGER.info("Registering user " + dto.email());

        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .email(dto.email())
                .displayName(dto.displayName())
                .admin(false)
                .passwordHash(encoder.encode(dto.password()))
                .points(0)
                .streakCount(0)
                .build();

        users.save(user);
        return ResponseEntity.ok(Map.of("message", "Registered", "success", true, "data", Map.of()));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> login(LoginDto dto) {
        var userOpt = users.findByEmail(dto.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "Invalid credentials"));
        }

        var user = userOpt.get();
        if (!encoder.matches(dto.password(), user.getPasswordHash())) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "Invalid credentials"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Logged in",
                "success", true,
                "data", Map.of("token", jwt.generate(user.getEmail()))));
    }
}
