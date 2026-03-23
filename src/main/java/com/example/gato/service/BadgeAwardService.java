package com.example.gato.service;

import com.example.gato.domain.badge.Badge;
import com.example.gato.domain.badge.UserBadge;
import com.example.gato.domain.badge.UserBadgeId;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.BadgeRepository;
import com.example.gato.repository.TodoRepository;
import com.example.gato.repository.UserBadgeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BadgeAwardService {

    private final AppUserRepository users;
    private final BadgeRepository badges;
    private final TodoRepository todos;
    private final UserBadgeRepository userBadges;

    @Async
    @Transactional
    public void evaluateUserAsync(UUID userId) {
        AppUser user = users.findById(userId).orElseThrow();
        long completedTodos = todos.countByUser_IdAndDoneTrue(user.getId());

        for (Badge badge : badges.findAll()) {
            if (!hasConditions(badge) || !meetsConditions(badge, user, completedTodos)) {
                continue;
            }
            if (userBadges.existsByUser_IdAndBadge_Id(user.getId(), badge.getId())) {
                continue;
            }

            userBadges.save(UserBadge.builder()
                    .id(new UserBadgeId(user.getId(), badge.getId()))
                    .user(user)
                    .badge(badge)
                    .build());
        }
    }

    private boolean hasConditions(Badge badge) {
        return badge.getMinPoints() != null
                || badge.getMinStreakCount() != null
                || badge.getMinCompletedTodos() != null;
    }

    private boolean meetsConditions(Badge badge, AppUser user, long completedTodos) {
        return matchesThreshold(user.getPoints(), badge.getMinPoints())
                && matchesThreshold(user.getStreakCount(), badge.getMinStreakCount())
                && matchesThreshold(completedTodos, badge.getMinCompletedTodos());
    }

    private boolean matchesThreshold(long actual, Integer required) {
        return required == null || actual >= required;
    }
}
