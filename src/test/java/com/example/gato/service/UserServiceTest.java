package com.example.gato.service;

import com.example.gato.api.me.dto.UserProfileDto;
import com.example.gato.domain.badge.Badge;
import com.example.gato.domain.badge.UserBadge;
import com.example.gato.domain.badge.UserBadgeId;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.UserBadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository users;

    @Mock
    private UserBadgeRepository userBadges;

    @InjectMocks
    private UserService service;

    @Test
    void getProfileReturnsUserSnapshotWithBadgeCodes() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder()
                .id(userId)
                .email("user@example.com")
                .displayName("User")
                .passwordHash("hash")
                .points(70)
                .streakCount(5)
                .build();
        UserBadge badge = UserBadge.builder()
                .id(new UserBadgeId(userId, UUID.randomUUID()))
                .user(user)
                .badge(Badge.builder().id(UUID.randomUUID()).code("STREAK_7").name("Seven Day Streak").build())
                .build();
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userBadges.findByUser_Id(userId)).thenReturn(List.of(badge));

        UserProfileDto result = service.getProfile("user@example.com");

        assertThat(result).isEqualTo(new UserProfileDto(
                "user@example.com",
                "User",
                70,
                5,
                List.of("STREAK_7")));
    }
}
