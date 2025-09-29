package com.example.gato.api.todo;

import com.example.gato.api.todo.dto.CreateTodoDto;
import com.example.gato.api.todo.dto.TodoDto;
import com.example.gato.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todos;

    @GetMapping
    public List<TodoDto> list(Principal p) {
        return todos.list(p.getName());
    }

    @PostMapping
    public TodoDto create(Principal p, @Valid @RequestBody CreateTodoDto dto) {
        return todos.create(p.getName(), dto);
    }

    @PatchMapping("/{id}/complete")
    public TodoDto complete(Principal p, @PathVariable UUID id) {
        return todos.complete(p.getName(), id);
    }
}
