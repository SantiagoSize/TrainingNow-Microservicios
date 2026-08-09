package com.tn.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Ejercicio de la biblioteca. Campos alineados al contrato del cliente Android (ExerciseDto).
 */
@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "La categoría es obligatoria")
    private String category; // Pectorales, Espalda, Piernas, etc.

    @Column(length = 2000)
    private String description;

    private String videoUrl;

    /** true = ejercicio del sistema; false = creado por un usuario/coach. */
    @Builder.Default
    private Boolean isSystemDefault = true;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt;

    @PrePersist
    void onCreate() {
        long now = System.currentTimeMillis();
        createdAt = now;
        updatedAt = now;
        if (isSystemDefault == null) isSystemDefault = true;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
