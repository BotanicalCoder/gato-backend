package com.example.gato.service;

import com.example.gato.api.auth.dto.LoginDto;
import com.example.gato.api.auth.dto.RegisterDto;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository users;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwt;

    @InjectMocks
    private AuthService service;

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterDto dto = new RegisterDto("user@example.com", "User", "Strong1!");
        when(users.existsByEmail("user@example.com")).thenReturn(true);

        ResponseEntity<?> response = service.register(dto);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("success")).isEqualTo(false);
        verify(users, never()).save(any());
    }

    @Test
    void registerEncodesPasswordAndSavesUser() {
        RegisterDto dto = new RegisterDto("user@example.com", "User", "Strong1!");
        when(users.existsByEmail("user@example.com")).thenReturn(false);
        when(encoder.encode("Strong1!")).thenReturn("encoded-password");

        ResponseEntity<?> response = service.register(dto);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(users).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("user@example.com");
        assertThat(captor.getValue().getDisplayName()).isEqualTo("User");
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("encoded-password");
        assertThat(captor.getValue().isAdmin()).isFalse();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void loginReturnsUnauthorizedWhenPasswordDoesNotMatch() {
        AppUser user = AppUser.builder()
                .email("user@example.com")
                .passwordHash("stored-hash")
                .build();
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong-password", "stored-hash")).thenReturn(false);

        ResponseEntity<?> response = service.login(new LoginDto("user@example.com", "wrong-password"));

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message")).isEqualTo("Invalid credentials");
        verifyNoInteractions(jwt);
    }

    @Test
    void loginReturnsJwtForValidCredentials() {
        AppUser user = AppUser.builder()
                .email("user@example.com")
                .passwordHash("stored-hash")
                .build();
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("Strong1!", "stored-hash")).thenReturn(true);
        when(jwt.generate("user@example.com")).thenReturn("jwt-token");

        ResponseEntity<?> response = service.login(new LoginDto("user@example.com", "Strong1!"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("success")).isEqualTo(true);
        Map<?, ?> data = (Map<?, ?>) body.get("data");
        assertThat(data.get("token")).isEqualTo("jwt-token");
    }
}
