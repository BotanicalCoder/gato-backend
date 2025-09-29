package com.example.gato.api.auth;

import com.example.gato.api.auth.dto.LoginDto;
import com.example.gato.api.auth.dto.RegisterDto;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto dto) {
        if (users.existsByEmail(dto.email()))
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Email already in use", "success", false, "data", Map.of()));

        Logger.getLogger(AuthController.class.getName()).info("Registering user " + dto.email());

        AppUser u = AppUser.builder()
                .id(UUID.randomUUID())
                .email(dto.email())
                .displayName(dto.displayName())
                .passwordHash(encoder.encode(dto.password()))
                .points(0)
                .streakCount(0)
                .build();

        users.save(u);
        return ResponseEntity.ok(Map.of("message", "Registered", "success", true, "data", Map.of()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto dto) {
        var uOpt = users.findByEmail(dto.email());
        if (uOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials"));
        }
        var u = uOpt.get();
        if (!encoder.matches(dto.password(), u.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials"));
        }

        Map<String, Object> body = Map.of(
                "message", "Logged in",
                "success", true,
                "data", Map.of("token", jwt.generate(u.getEmail())));
        return ResponseEntity.ok(body);

    }
}
