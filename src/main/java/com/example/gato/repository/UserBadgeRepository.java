package com.example.gato.repository;

import com.example.gato.domain.badge.UserBadge;
import com.example.gato.domain.badge.UserBadgeId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserBadgeRepository extends JpaRepository<UserBadge, UserBadgeId> {
    long countByUser_Id(java.util.UUID userId);

    boolean existsByUser_IdAndBadge_Id(java.util.UUID userId, java.util.UUID badgeId);

    List<com.example.gato.domain.badge.UserBadge> findByUser_Id(UUID userId);
}
