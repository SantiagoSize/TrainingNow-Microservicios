package com.tn.rutinas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Ejercicio dentro de una rutina (referencia externa a tn-biblioteca).
 */
@Entity
@Table(name = "routine_exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EjercicioRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long routineId;

    @Column(nullable = false)
    @NotNull
    private Long exerciseId;

    /** Orden del ejercicio dentro de la rutina. */
    @Column(name = "exercise_order", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;
}
