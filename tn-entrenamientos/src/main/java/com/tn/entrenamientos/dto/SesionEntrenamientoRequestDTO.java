package com.tn.entrenamientos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Petición para registrar una sesión de entrenamiento completada")
public class SesionEntrenamientoRequestDTO {

    @NotNull
    @Schema(description = "ID del usuario que realiza la sesión", example = "1")
    private Long userId;

    @NotNull
    @Schema(description = "ID del ejercicio de la rutina (rutinaEjercicioId)", example = "5")
    private Long rutinaEjercicioId;

    @NotNull
    @Min(1)
    @Schema(description = "Series realizadas", example = "3")
    private Integer seriesRealizadas;

    @NotNull
    @Min(1)
    @Schema(description = "Repeticiones por serie realizadas", example = "10")
    private Integer repeticionesPorSerie;

    @NotNull
    @Min(1)
    @Schema(description = "Peso levantado en kilogramos", example = "60")
    private Double pesoLevantado;
}

