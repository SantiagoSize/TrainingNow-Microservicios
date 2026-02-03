package com.tn.biblioteca.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entidad de ejercicio de la biblioteca de entrenamiento.
 */
@Entity
@Table(name = "ejercicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ejercicio de la biblioteca con grupo muscular, dificultad y equipamiento")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del ejercicio", example = "1")
    private Long id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del ejercicio", example = "Press de banca", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción detallada del ejercicio y técnica", example = "Tumbado en banco, bajar la barra al pecho y extender.")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular", nullable = false, length = 30)
    @NotNull(message = "El grupo muscular es obligatorio")
    @Schema(description = "Grupo muscular principal (PECHO, ESPALDA, PIERNAS, HOMBROS, BRAZOS, CORE)", requiredMode = Schema.RequiredMode.REQUIRED)
    private GrupoMuscular grupoMuscular;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "La dificultad es obligatoria")
    @Schema(description = "Nivel de dificultad (PRINCIPIANTE, INTERMEDIO, AVANZADO)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Dificultad dificultad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "El equipamiento es obligatorio")
    @Schema(description = "Equipamiento necesario (MANCUERNAS, BARRA, MAQUINA, PESO_CORPORAL)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Equipamiento equipamiento;

    @Column(name = "url_video", length = 500)
    @Schema(description = "URL del video demostrativo del ejercicio", example = "https://example.com/video/press-banca")
    private String urlVideo;

    @Column(name = "calorias_estimadas")
    @Schema(description = "Calorías estimadas quemadas por minuto", example = "8.5")
    private Double caloriasEstimadas;
}
