package com.example.gato.service;

import com.example.gato.api.todo.dto.CreateTodoDto;
import com.example.gato.api.todo.dto.TodoDto;
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
    private final BadgeAwardService badgeAwards;

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

            users.save(u);
            badgeAwards.evaluateUserAsync(u.getId());
        }

        return map(t);
    }

    private TodoDto map(Todo t) {
        return new TodoDto(t.getId(), t.getTitle(), t.getNotes(), t.getDueOn(), t.isDone(), t.getDoneAt());
    }
}
