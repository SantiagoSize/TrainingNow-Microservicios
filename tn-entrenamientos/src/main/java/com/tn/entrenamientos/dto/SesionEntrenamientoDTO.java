package com.tn.entrenamientos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Registro de una sesión de entrenamiento completada")
public class SesionEntrenamientoDTO {

    @Schema(description = "ID de la sesión", example = "1")
    private Long id;

    @Schema(description = "ID del usuario que realizó la sesión", example = "1")
    private Long userId;

    @Schema(description = "ID del ejercicio de la rutina", example = "5")
    private Long rutinaEjercicioId;

    @Schema(description = "Fecha y hora de la sesión", example = "2026-02-03T10:00:00")
    private LocalDateTime fechaHora;

    @Schema(description = "Series realizadas", example = "3")
    private Integer seriesRealizadas;

    @Schema(description = "Repeticiones por serie realizadas", example = "10")
    private Integer repeticionesPorSerie;

    @Schema(description = "Peso levantado en kilogramos", example = "60")
    private Double pesoLevantado;
}

