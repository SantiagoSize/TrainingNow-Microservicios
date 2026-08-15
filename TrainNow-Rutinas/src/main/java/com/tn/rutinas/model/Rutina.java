package com.tn.rutinas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Rutina de entrenamiento. Campos alineados al contrato del cliente Android (RoutineDto).
 * ownerId null = rutina pública/global.
 */
@Entity
@Table(name = "routines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Dueño de la rutina (referencia externa a tn-usuarios). Null = pública. */
    private Long ownerId;

    /** Creador de la rutina (coach o usuario). */
    @Column(nullable = false)
    private Long creatorId;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    /** Info de días, ej. "Lunes, Miércoles, Viernes". */
    private String dayInfo;

    private Long creationDate;

    /** Hora programada del recordatorio (epoch millis). */
    private Long scheduledTime;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt;

    /** true = un entrenador la compartió con ownerId pero el usuario todavía no la acepta.
     *  No debe aparecer en "Mis rutinas" hasta que pase a false. */
    @Builder.Default
    private Boolean pendingShare = false;

    /** true = plantilla reutilizable de un entrenador (ownerId null, no es una rutina global
     *  admin). El entrenador la comparte con distintos usuarios sin volver a crearla. */
    @Builder.Default
    private Boolean isTemplate = false;

    @PrePersist
    void onCreate() {
        long now = System.currentTimeMillis();
        createdAt = now;
        updatedAt = now;
        if (creationDate == null) creationDate = now;
        if (pendingShare == null) pendingShare = false;
        if (isTemplate == null) isTemplate = false;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
