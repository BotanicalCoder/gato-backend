package com.example.gato.repository;

import com.example.gato.domain.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
