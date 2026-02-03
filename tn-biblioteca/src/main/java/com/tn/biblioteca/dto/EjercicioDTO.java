package com.tn.biblioteca.dto;

import com.tn.biblioteca.model.Dificultad;
import com.tn.biblioteca.model.Equipamiento;
import com.tn.biblioteca.model.GrupoMuscular;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un ejercicio de la biblioteca")
public class EjercicioDTO {

    @Schema(description = "Identificador único del ejercicio", example = "1")
    private Long id;

    @Schema(description = "Nombre del ejercicio", example = "Sentadilla Goblet", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(
            description = "Descripción detallada del ejercicio y técnica",
            example = "Sujeta una mancuerna frente al pecho y desciende en sentadilla profunda manteniendo la espalda recta."
    )
    private String descripcion;

    @Schema(
            description = "Grupo muscular principal que trabaja el ejercicio (PECHO, ESPALDA, PIERNAS, HOMBROS, BRAZOS, CORE)",
            example = "PIERNAS",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private GrupoMuscular grupoMuscular;

    @Schema(
            description = "Nivel de dificultad (PRINCIPIANTE, INTERMEDIO, AVANZADO)",
            example = "INTERMEDIO",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Dificultad dificultad;

    @Schema(
            description = "Equipamiento necesario (MANCUERNAS, BARRA, MAQUINA, PESO_CORPORAL)",
            example = "MANCUERNAS",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Equipamiento equipamiento;

    @Schema(description = "URL del video demostrativo", example = "https://example.com/video/sentadilla-goblet")
    private String urlVideo;

    @Schema(description = "Calorías estimadas quemadas por minuto", example = "7.5")
    private Double caloriasEstimadas;
}
