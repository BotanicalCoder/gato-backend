package com.example.gato.service;

import com.example.gato.api.admin.dto.AdminBadgeDto;
import com.example.gato.api.admin.dto.BadgeUpsertDto;
import com.example.gato.domain.badge.Badge;
import com.example.gato.repository.BadgeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminBadgeService {

    private final BadgeRepository badges;

    @Transactional
    public List<AdminBadgeDto> list() {
        return badges.findAll().stream().map(this::map).toList();
    }

    @Transactional
    public AdminBadgeDto get(UUID id) {
        return map(findBadge(id));
    }

    public AdminBadgeDto create(BadgeUpsertDto dto) {
        if (badges.existsByCode(dto.code())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Badge code already exists");
        }

        Badge badge = Badge.builder()
                .id(UUID.randomUUID())
                .code(dto.code())
                .name(dto.name())
                .description(dto.description())
                .minPoints(dto.minPoints())
                .minStreakCount(dto.minStreakCount())
                .minCompletedTodos(dto.minCompletedTodos())
                .build();

        return map(badges.save(badge));
    }

    public AdminBadgeDto update(UUID id, BadgeUpsertDto dto) {
        Badge badge = findBadge(id);
        if (!badge.getCode().equals(dto.code()) && badges.existsByCode(dto.code())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Badge code already exists");
        }

        badge.setCode(dto.code());
        badge.setName(dto.name());
        badge.setDescription(dto.description());
        badge.setMinPoints(dto.minPoints());
        badge.setMinStreakCount(dto.minStreakCount());
        badge.setMinCompletedTodos(dto.minCompletedTodos());
        return map(badge);
    }

    public void delete(UUID id) {
        badges.delete(findBadge(id));
    }

    private Badge findBadge(UUID id) {
        return badges.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge not found"));
    }

    private AdminBadgeDto map(Badge badge) {
        return new AdminBadgeDto(
                badge.getId(),
                badge.getCode(),
                badge.getName(),
                badge.getDescription(),
                badge.getMinPoints(),
                badge.getMinStreakCount(),
                badge.getMinCompletedTodos());
    }
}
