package com.example.gato.service;

import com.example.gato.api.todo.dto.CreateTodoDto;
import com.example.gato.api.todo.dto.TodoDto;
import com.example.gato.domain.todo.Todo;
import com.example.gato.domain.user.AppUser;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private AppUserRepository users;

    @Mock
    private TodoRepository todos;

    @Mock
    private BadgeAwardService badgeAwards;

    @InjectMocks
    private TodoService service;

    @Test
    void listMapsTodosForCurrentUser() {
        AppUser user = user("user@example.com");
        Todo todo = Todo.builder()
                .id(UUID.randomUUID())
                .user(user)
                .title("Pay bills")
                .notes("Before 5 PM")
                .dueOn(LocalDate.now().plusDays(1))
                .done(false)
                .build();
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(todos.findByUser_Id(user.getId())).thenReturn(List.of(todo));

        List<TodoDto> result = service.list("user@example.com");

        assertThat(result).containsExactly(new TodoDto(
                todo.getId(),
                "Pay bills",
                "Before 5 PM",
                todo.getDueOn(),
                false,
                null));
    }

    @Test
    void createBuildsTodoForResolvedUser() {
        AppUser user = user("user@example.com");
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(todos.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TodoDto result = service.create("user@example.com",
                new CreateTodoDto("Pay bills", "Before 5 PM", LocalDate.of(2026, 3, 24)));

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(todos).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(user);
        assertThat(captor.getValue().getTitle()).isEqualTo("Pay bills");
        assertThat(captor.getValue().isDone()).isFalse();
        assertThat(result.title()).isEqualTo("Pay bills");
    }

    @Test
    void completeAwardsPointsUpdatesStreakAndTriggersBadges() {
        AppUser user = user("user@example.com");
        user.setPoints(20);
        user.setStreakCount(2);
        user.setLastDoneOn(LocalDate.now().minusDays(1));
        Todo todo = Todo.builder()
                .id(UUID.randomUUID())
                .user(user)
                .title("Pay bills")
                .done(false)
                .build();
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(todos.findByIdAndUser_Id(todo.getId(), user.getId())).thenReturn(Optional.of(todo));

        TodoDto result = service.complete("user@example.com", todo.getId());

        assertThat(todo.isDone()).isTrue();
        assertThat(todo.getDoneAt()).isNotNull();
        assertThat(user.getPoints()).isEqualTo(30);
        assertThat(user.getStreakCount()).isEqualTo(3);
        assertThat(user.getLastDoneOn()).isEqualTo(LocalDate.now());
        assertThat(result.done()).isTrue();
        verify(users).save(user);
        verify(badgeAwards).evaluateUserAsync(user.getId());
    }

    @Test
    void completeLeavesAlreadyDoneTodoUnchanged() {
        AppUser user = user("user@example.com");
        user.setPoints(20);
        user.setStreakCount(2);
        Instant doneAt = Instant.now().minusSeconds(120);
        Todo todo = Todo.builder()
                .id(UUID.randomUUID())
                .user(user)
                .title("Pay bills")
                .done(true)
                .doneAt(doneAt)
                .build();
        when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(todos.findByIdAndUser_Id(todo.getId(), user.getId())).thenReturn(Optional.of(todo));

        TodoDto result = service.complete("user@example.com", todo.getId());

        assertThat(result.doneAt()).isEqualTo(doneAt);
        assertThat(user.getPoints()).isEqualTo(20);
        verify(users, never()).save(any());
        verifyNoInteractions(badgeAwards);
    }

    private AppUser user(String email) {
        return AppUser.builder()
                .id(UUID.randomUUID())
                .email(email)
                .displayName("User")
                .passwordHash("hash")
                .build();
    }
}
