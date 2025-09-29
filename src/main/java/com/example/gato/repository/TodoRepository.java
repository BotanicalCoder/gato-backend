package com.example.gato.repository;

import com.example.gato.domain.todo.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findByUser_Id(UUID userId);

    Optional<Todo> findByIdAndUser_Id(UUID id, UUID userId);
}
