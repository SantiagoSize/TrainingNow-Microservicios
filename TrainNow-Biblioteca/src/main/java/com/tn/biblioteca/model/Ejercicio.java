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

    /** Imagen del ejercicio: URL pública o data URI comprimido (JPEG base64, máx ~120 KB).
     *  MEDIUMTEXT porque TEXT de MySQL solo llega a 65 535 bytes: una foto real comprimida
     *  (800px, JPEG) en base64 ya se acerca o supera ese límite (mismo problema que
     *  profilePhotoUrl en TrainNow-Usuarios). */
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageUrl;

    /** Músculos trabajados (CSV). */
    private String muscles;

    /** Nivel: PRINCIPIANTE, INTERMEDIO, AVANZADO. */
    @Builder.Default
    private String difficulty = "PRINCIPIANTE";

    /** Equipamiento necesario. */
    private String equipment;

    /** Formas alternativas de hacerlo (ej. "Mancuernas, Barra, Máquina"), separadas por coma. */
    @Column(columnDefinition = "TEXT")
    private String alternatives;

    /** Pasos de ejecución, separados por "|". */
    @Column(columnDefinition = "TEXT")
    private String instructions;

    /** Consejos de técnica, separados por "|". */
    @Column(columnDefinition = "TEXT")
    private String tips;

    /** Errores frecuentes, separados por "|". */
    @Column(columnDefinition = "TEXT")
    private String commonMistakes;

    /** Series recomendadas. */
    @Builder.Default
    private Integer recommendedSets = 3;

    /** Repeticiones recomendadas (texto: "8-12", "30 seg"). */
    private String recommendedReps;

    /** Descanso recomendado entre series, en segundos. */
    @Builder.Default
    private Integer restSeconds = 60;

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
