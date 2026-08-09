package com.tn.rutinas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Registro de un ejercicio dentro de una sesión. Contrato: ExerciseLogDto (Android).
 */
@Entity
@Table(name = "exercise_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long sessionId;

    @Column(nullable = false)
    @NotNull
    private Long exerciseId;

    @Builder.Default
    private Integer orderInSession = 0;

    @Builder.Default
    private Integer plannedSets = 3;

    @Builder.Default
    private Integer plannedReps = 12;

    private Double plannedWeightKg;

    @Builder.Default
    private Integer completedSets = 0;

    /** Reps reales por serie, CSV ej. "12,10,8". */
    private String actualReps;

    private Double weightKg;

    @Builder.Default
    private Integer restTimeSeconds = 60;

    private Integer durationSeconds;

    @Column(length = 1000)
    private String notes;

    private Integer rpe;

    @Builder.Default
    private Boolean isPersonalRecord = false;

    private Integer formRating;
    private String tempo;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @PrePersist
    void onCreate() {
        createdAt = System.currentTimeMillis();
        if (isPersonalRecord == null) isPersonalRecord = false;
    }
}
