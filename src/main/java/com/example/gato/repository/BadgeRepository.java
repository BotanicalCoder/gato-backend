package com.example.gato.repository;

import com.example.gato.domain.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface BadgeRepository extends JpaRepository<Badge, UUID> {
    Optional<Badge> findByCode(String code);

    boolean existsByCode(String code);
}
