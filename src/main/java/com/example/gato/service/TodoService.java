package com.example.gato.service;

import com.example.gato.api.todo.dto.CreateTodoDto;
import com.example.gato.api.todo.dto.TodoDto;
import com.example.gato.domain.badge.Badge;
import com.example.gato.domain.badge.UserBadge;
import com.example.gato.domain.badge.UserBadgeId;
import com.example.gato.domain.todo.Todo;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final AppUserRepository users;
    private final TodoRepository todos;
    private final BadgeRepository badges;
    private final UserBadgeRepository userBadges;

    public List<TodoDto> list(String email) {
        AppUser u = users.findByEmail(email).orElseThrow();
        return todos.findByUser_Id(u.getId()).stream().map(this::map).toList();
    }

    public TodoDto create(String email, CreateTodoDto dto) {
        AppUser u = users.findByEmail(email).orElseThrow();
        Todo t = Todo.builder()
                .id(UUID.randomUUID())
                .user(u)
                .title(dto.title())
                .notes(dto.notes())
                .dueOn(dto.dueOn())
                .done(false)
                .build();
        return map(todos.save(t));
    }

    public TodoDto complete(String email, UUID id) {
        AppUser u = users.findByEmail(email).orElseThrow();
        Todo t = todos.findByIdAndUser_Id(id, u.getId()).orElseThrow();

        if (!t.isDone()) {
            t.setDone(true);
            t.setDoneAt(Instant.now());

            // Points
            u.setPoints(u.getPoints() + 10);

            // Streak
            LocalDate today = LocalDate.now();
            if (u.getLastDoneOn() != null && u.getLastDoneOn().plusDays(1).equals(today)) {
                u.setStreakCount(u.getStreakCount() + 1);
            } else {
                u.setStreakCount(1);
            }
            u.setLastDoneOn(today);

            // Badges
            maybeAward(u, "FIRST_DONE");
            if (u.getStreakCount() == 7)
                maybeAward(u, "STREAK_7");

            users.save(u);
        }

        return map(t);
    }

    private void maybeAward(AppUser u, String code) {
        Badge b = badges.findByCode(code).orElse(null);
        if (b == null)
            return;
        if (userBadges.existsByUser_IdAndBadge_Id(u.getId(), b.getId()))
            return;

        userBadges.save(UserBadge.builder()
                .id(new UserBadgeId(u.getId(), b.getId()))
                .user(u)
                .badge(b)
                .awardedAt(Instant.now())
                .build());
    }

    private TodoDto map(Todo t) {
        return new TodoDto(t.getId(), t.getTitle(), t.getNotes(), t.getDueOn(), t.isDone(), t.getDoneAt());
    }
}
