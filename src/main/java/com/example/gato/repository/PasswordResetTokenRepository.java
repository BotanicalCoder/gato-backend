package com.example.gato.repository;

import com.example.gato.domain.password.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
}
