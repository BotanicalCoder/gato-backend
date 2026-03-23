package com.example.gato.service;

import com.example.gato.domain.badge.Badge;
import com.example.gato.domain.badge.UserBadge;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.BadgeRepository;
import com.example.gato.repository.TodoRepository;
import com.example.gato.repository.UserBadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeAwardServiceTest {

    @Mock
    private AppUserRepository users;

    @Mock
    private BadgeRepository badges;

    @Mock
    private TodoRepository todos;

    @Mock
    private UserBadgeRepository userBadges;

    @InjectMocks
    private BadgeAwardService service;

    @Test
    void evaluateUserAsyncAwardsOnlyEligibleMissingBadges() {
        UUID userId = UUID.randomUUID();
        Badge completedBadge = Badge.builder()
                .id(UUID.randomUUID())
                .code("FIRST_DONE")
                .minCompletedTodos(1)
                .build();
        Badge streakBadge = Badge.builder()
                .id(UUID.randomUUID())
                .code("STREAK_7")
                .minStreakCount(7)
                .build();
        AppUser user = AppUser.builder()
                .id(userId)
                .email("user@example.com")
                .displayName("User")
                .passwordHash("hash")
                .points(50)
                .streakCount(7)
                .build();

        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(todos.countByUser_IdAndDoneTrue(userId)).thenReturn(3L);
        when(badges.findAll()).thenReturn(List.of(completedBadge, streakBadge));
        when(userBadges.existsByUser_IdAndBadge_Id(userId, completedBadge.getId())).thenReturn(false);
        when(userBadges.existsByUser_IdAndBadge_Id(userId, streakBadge.getId())).thenReturn(true);

        service.evaluateUserAsync(userId);

        ArgumentCaptor<UserBadge> captor = ArgumentCaptor.forClass(UserBadge.class);
        verify(userBadges).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(user);
        assertThat(captor.getValue().getBadge()).isSameAs(completedBadge);
        assertThat(captor.getValue().getId().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getId().getBadgeId()).isEqualTo(completedBadge.getId());
        verify(userBadges, never()).save(argThat(userBadge -> userBadge.getBadge().getId().equals(streakBadge.getId())));
    }

    @Test
    void evaluateUserAsyncSkipsBadgesWithoutConditions() {
        UUID userId = UUID.randomUUID();
        Badge manualBadge = Badge.builder()
                .id(UUID.randomUUID())
                .code("MANUAL")
                .name("Manual")
                .build();
        AppUser user = AppUser.builder()
                .id(userId)
                .email("user@example.com")
                .displayName("User")
                .passwordHash("hash")
                .build();

        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(todos.countByUser_IdAndDoneTrue(userId)).thenReturn(0L);
        when(badges.findAll()).thenReturn(List.of(manualBadge));

        service.evaluateUserAsync(userId);

        verify(userBadges, never()).save(any());
    }
}
