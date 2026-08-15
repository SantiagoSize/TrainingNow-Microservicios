package com.tn.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Registro de actividad administrativa: quién hizo qué y cuándo.
 * Se usa para auditar creaciones/ediciones en la biblioteca de ejercicios,
 * renombrado de categorías, sanciones a usuarios (ban/suspensión) y
 * publicación/edición de rutinas globales.
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Quién realizó la acción. */
    @Column(nullable = false)
    private Long actorId;

    @Column(nullable = false)
    private String actorName;

    @Column(nullable = false)
    private String actorRole;

    /** Acción realizada, ej: "EXERCISE_CREATED", "USER_BANNED", "CATEGORY_RENAMED". */
    @Column(nullable = false)
    private String action;

    /** Tipo de entidad afectada, ej: "EXERCISE", "USER", "CATEGORY", "ROUTINE". */
    private String targetType;

    private Long targetId;

    private String targetName;

    /** Texto libre con detalles (motivo de sanción, duración, nombre anterior/nuevo, etc.). */
    @Column(length = 1000)
    private String details;

    @Column(nullable = false, updatable = false)
    private Long timestamp;

    @PrePersist
    void onCreate() {
        if (timestamp == null) timestamp = System.currentTimeMillis();
    }
}
