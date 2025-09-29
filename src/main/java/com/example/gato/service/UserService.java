package com.example.gato.service;

import com.example.gato.api.me.dto.UserProfileDto;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final AppUserRepository users;
    private final UserBadgeRepository userBadges;

    public UserProfileDto getProfile(String email) {
        AppUser u = users.findByEmail(email).orElseThrow();
        List<String> badgeCodes = userBadges.findByUser_Id(u.getId())
                .stream()
                .map(ub -> ub.getBadge().getCode())
                .toList();

        return new UserProfileDto(
                u.getEmail(),
                u.getDisplayName(),
                u.getPoints(),
                u.getStreakCount(),
                badgeCodes);
    }
}
