package com.tn.rutinas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Registro de asistencia de un usuario en un día concreto.
 * Permite construir el reporte mensual (días entrenados vs. días que lo dejó).
 */
@Entity
@Table(name = "attendance_days",
       uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long userId;

    /** Fecha en formato yyyy-MM-dd (día natural del usuario). */
    @Column(nullable = false, length = 10)
    @NotNull
    private String date;

    /** TRAINED = entrenó, REST = día de descanso, MISSED = tenía plan y no entrenó. */
    @Column(nullable = false, length = 10)
    @Builder.Default
    private String status = "TRAINED";

    private Long routineId;

    /** Cantidad de ejercicios completados ese día. */
    @Builder.Default
    private Integer exercisesCompleted = 0;

    private Integer durationMinutes;

    @Column(nullable = false)
    private Long createdAt;

    @PrePersist
    void onCreate() {
        createdAt = System.currentTimeMillis();
        if (status == null) status = "TRAINED";
        if (exercisesCompleted == null) exercisesCompleted = 0;
    }
}
