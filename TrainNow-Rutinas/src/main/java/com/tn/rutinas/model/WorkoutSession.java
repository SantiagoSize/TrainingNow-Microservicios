package com.tn.rutinas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Sesión de entrenamiento. Contrato: WorkoutSessionDto (Android).
 */
@Entity
@Table(name = "workout_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long userId;

    private Long routineId;

    private Long startTime;
    private Long endTime;

    @Column(nullable = false)
    @Builder.Default
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED, CANCELLED

    private Integer totalDurationMinutes;
    private Integer caloriesBurned;

    @Column(length = 1000)
    private String notes;

    private Integer rating;
    private Integer perceivedDifficulty;
    private String mood;
    private String location;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @PrePersist
    void onCreate() {
        createdAt = System.currentTimeMillis();
        if (startTime == null) startTime = createdAt;
    }
}
