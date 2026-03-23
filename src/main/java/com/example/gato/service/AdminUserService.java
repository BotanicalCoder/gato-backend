package com.example.gato.service;

import com.example.gato.api.admin.dto.AdminUserDto;
import com.example.gato.api.admin.dto.UpdateUserDto;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.UserBadgeRepository;
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
public class AdminUserService {

    private final AppUserRepository users;
    private final UserBadgeRepository userBadges;
    private final BadgeAwardService badgeAwards;

    @Transactional
    public List<AdminUserDto> list() {
        return users.findAll().stream().map(this::map).toList();
    }

    @Transactional
    public AdminUserDto get(UUID id) {
        return map(findUser(id));
    }

    public AdminUserDto update(UUID id, UpdateUserDto dto) {
        AppUser user = findUser(id);
        user.setDisplayName(dto.displayName());
        user.setPoints(dto.points());
        user.setStreakCount(dto.streakCount());
        badgeAwards.evaluateUserAsync(user.getId());
        return map(user);
    }

    public AdminUserDto updateAdminStatus(UUID id, boolean admin) {
        AppUser user = findUser(id);
        user.setAdmin(admin);
        return map(user);
    }

    public void delete(UUID id) {
        users.delete(findUser(id));
    }

    private AppUser findUser(UUID id) {
        return users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private AdminUserDto map(AppUser user) {
        List<String> badges = userBadges.findByUser_Id(user.getId()).stream()
                .map(userBadge -> userBadge.getBadge().getCode())
                .toList();

        return new AdminUserDto(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.isAdmin(),
                user.getPoints(),
                user.getStreakCount(),
                badges);
    }
}
