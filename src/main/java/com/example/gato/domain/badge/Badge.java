package com.example.gato.domain.badge;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "badge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "min_points")
    private Integer minPoints;

    @Column(name = "min_streak_count")
    private Integer minStreakCount;

    @Column(name = "min_completed_todos")
    private Integer minCompletedTodos;
}
