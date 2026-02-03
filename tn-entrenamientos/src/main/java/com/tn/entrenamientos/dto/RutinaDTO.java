package com.tn.entrenamientos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Representación de una rutina de entrenamiento")
public class RutinaDTO {

    @Schema(description = "ID de la rutina", example = "1")
    private Long id;

    @Schema(description = "ID del atleta (owner) que utiliza la rutina", example = "1")
    private Long ownerId;

    @Schema(description = "ID del creador de la rutina (por ejemplo, el entrenador)", example = "10")
    private Long creatorId;

    @Schema(description = "Nombre de la rutina", example = "Rutina de Inicio - Full Body")
    private String nombre;

    @Schema(description = "Descripción general de la rutina", example = "Sesión full body para principiantes, 3 días por semana.")
    private String descripcion;

    @Schema(description = "Fecha de creación de la rutina", example = "2026-02-03T10:00:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Ejercicios que componen la rutina")
    private List<RutinaEjercicioDTO> ejercicios;
}

