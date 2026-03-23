package com.example.gato.api.auth;

import com.example.gato.api.auth.dto.ForgotPasswordRequest;
import com.example.gato.api.auth.dto.ResetPasswordRequest;
import com.example.gato.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@Valid @RequestBody ForgotPasswordRequest req) {
        passwordResetService.requestReset(req.email());
        return ResponseEntity.ok(Map.of("message", "If the email exists, a reset token was sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@Valid @RequestBody ResetPasswordRequest req) {
        passwordResetService.resetPassword(req.token(), req.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset"));
    }
}
