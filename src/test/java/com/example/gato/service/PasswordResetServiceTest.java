package com.example.gato.service;

import com.example.gato.domain.password.PasswordResetToken;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private AppUserRepository users;

    @Mock
    private PasswordResetTokenRepository tokens;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordResetEmailTemplateService passwordResetEmailTemplateService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder encoder;

    @InjectMocks
    private PasswordResetService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "tokenTtlMinutes", 30L);
    }

    @Test
    void requestResetCreatesTokenAndSendsEmail() {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .displayName("User")
                .passwordHash("hash")
                .build();
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordResetEmailTemplateService.render(any(), any(), anyLong())).thenReturn("<html>reset-body</html>");

        service.requestReset("user@example.com");

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokens).save(tokenCaptor.capture());
        verify(passwordResetEmailTemplateService).render(eq("User"), eq(tokenCaptor.getValue().getToken()), eq(30L));
        verify(emailService).send("user@example.com", "Reset your password", "<html>reset-body</html>", true);
        assertThat(tokenCaptor.getValue().isUsed()).isFalse();
        assertThat(tokenCaptor.getValue().getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    void requestResetDoesNothingWhenUserDoesNotExist() {
        when(users.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        service.requestReset("missing@example.com");

        verify(tokens, never()).save(any());
        verifyNoInteractions(emailService);
    }

    @Test
    void resetPasswordUpdatesStoredPasswordAndMarksTokenUsed() {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .displayName("User")
                .passwordHash("old-hash")
                .build();
        PasswordResetToken token = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token("token-123")
                .expiresAt(Instant.now().plusSeconds(1800))
                .used(false)
                .build();

        when(tokens.findByToken("token-123")).thenReturn(Optional.of(token));
        when(encoder.encode("new-password")).thenReturn("encoded-password");

        service.resetPassword("token-123", "new-password");

        assertThat(user.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(token.isUsed()).isTrue();
        verify(tokens).save(token);
        verify(users).save(user);
    }

    @Test
    void resetPasswordRejectsExpiredToken() {
        PasswordResetToken token = PasswordResetToken.builder()
                .id(UUID.randomUUID())
                .token("expired-token")
                .expiresAt(Instant.now().minusSeconds(30))
                .used(false)
                .build();
        when(tokens.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> service.resetPassword("expired-token", "new-password"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Token invalid or expired");
    }
}
