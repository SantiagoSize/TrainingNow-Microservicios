package com.tn.entrenamientos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Ejercicio dentro de una rutina")
public class RutinaEjercicioDTO {

    @Schema(description = "ID del ejercicio (referencia al microservicio de ejercicios)", example = "10")
    private Long ejercicioId;

    @Schema(description = "Número de series", example = "4")
    private Integer series;

    @Schema(description = "Número de repeticiones", example = "12")
    private Integer repeticiones;

    @Schema(description = "Descanso entre series en segundos", example = "60")
    private Integer descanso;

    @Schema(description = "Observaciones del entrenador para este ejercicio", example = "Foco en técnica, evitar llegar al fallo.")
    private String observaciones;
}

