package com.example.gato.service;

import com.example.gato.api.admin.dto.AdminBadgeDto;
import com.example.gato.api.admin.dto.BadgeUpsertDto;
import com.example.gato.domain.badge.Badge;
import com.example.gato.repository.BadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminBadgeServiceTest {

    @Mock
    private BadgeRepository badges;

    @InjectMocks
    private AdminBadgeService service;

    @Test
    void listMapsBadgesToDtos() {
        Badge badge = Badge.builder()
                .id(UUID.randomUUID())
                .code("FIRST_DONE")
                .name("First Todo")
                .description("Awarded after one completed todo")
                .minCompletedTodos(1)
                .build();
        when(badges.findAll()).thenReturn(List.of(badge));

        List<AdminBadgeDto> result = service.list();

        assertThat(result).containsExactly(new AdminBadgeDto(
                badge.getId(),
                "FIRST_DONE",
                "First Todo",
                "Awarded after one completed todo",
                null,
                null,
                1));
    }

    @Test
    void createRejectsDuplicateCode() {
        BadgeUpsertDto dto = new BadgeUpsertDto("FIRST_DONE", "First Todo", "desc", 10, 1, 1);
        when(badges.existsByCode("FIRST_DONE")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Badge code already exists");

        verify(badges, never()).save(any());
    }

    @Test
    void createPersistsAndReturnsSavedBadge() {
        BadgeUpsertDto dto = new BadgeUpsertDto("FIRST_DONE", "First Todo", "desc", 10, 1, 1);
        when(badges.existsByCode("FIRST_DONE")).thenReturn(false);
        when(badges.save(any(Badge.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminBadgeDto result = service.create(dto);

        ArgumentCaptor<Badge> captor = ArgumentCaptor.forClass(Badge.class);
        verify(badges).save(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("FIRST_DONE");
        assertThat(captor.getValue().getName()).isEqualTo("First Todo");
        assertThat(captor.getValue().getDescription()).isEqualTo("desc");
        assertThat(result.code()).isEqualTo("FIRST_DONE");
        assertThat(result.minPoints()).isEqualTo(10);
    }

    @Test
    void updateRejectsDuplicateCodeWhenChangingCode() {
        UUID badgeId = UUID.randomUUID();
        Badge existing = Badge.builder()
                .id(badgeId)
                .code("FIRST_DONE")
                .name("First Todo")
                .build();
        BadgeUpsertDto dto = new BadgeUpsertDto("STREAK_7", "Seven Day Streak", "desc", 50, 7, 5);
        when(badges.findById(badgeId)).thenReturn(Optional.of(existing));
        when(badges.existsByCode("STREAK_7")).thenReturn(true);

        assertThatThrownBy(() -> service.update(badgeId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Badge code already exists");
    }
}
