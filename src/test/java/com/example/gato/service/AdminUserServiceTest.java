package com.example.gato.service;

import com.example.gato.api.admin.dto.AdminUserDto;
import com.example.gato.api.admin.dto.UpdateUserDto;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private AppUserRepository users;

    @Mock
    private UserBadgeRepository userBadges;

    @Mock
    private BadgeAwardService badgeAwards;

    @InjectMocks
    private AdminUserService service;

    @Test
    void getMapsUserAndBadgeCodes() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder()
                .id(userId)
                .email("user@example.com")
                .displayName("User")
                .admin(true)
                .points(40)
                .streakCount(3)
                .passwordHash("hash")
                .build();
        UserBadge userBadge = UserBadge.builder()
                .id(new UserBadgeId(userId, UUID.randomUUID()))
                .user(user)
                .badge(Badge.builder().id(UUID.randomUUID()).code("FIRST_DONE").name("First Todo").build())
                .build();

        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(userBadges.findByUser_Id(userId)).thenReturn(List.of(userBadge));

        AdminUserDto result = service.get(userId);

        assertThat(result).isEqualTo(new AdminUserDto(
                userId,
                "user@example.com",
                "User",
                true,
                40,
                3,
                List.of("FIRST_DONE")));
    }

    @Test
    void updateMutatesUserAndTriggersBadgeEvaluation() {
        UUID userId = UUID.randomUUID();
        AppUser user = AppUser.builder()
                .id(userId)
                .email("user@example.com")
                .displayName("Old Name")
                .admin(false)
                .points(10)
                .streakCount(1)
                .passwordHash("hash")
                .build();
        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(userBadges.findByUser_Id(userId)).thenReturn(List.of());

        AdminUserDto result = service.update(userId, new UpdateUserDto("New Name", 25, 4));

        assertThat(user.getDisplayName()).isEqualTo("New Name");
        assertThat(user.getPoints()).isEqualTo(25);
        assertThat(user.getStreakCount()).isEqualTo(4);
        assertThat(result.displayName()).isEqualTo("New Name");
        verify(badgeAwards).evaluateUserAsync(userId);
    }
}
