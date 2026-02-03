package com.tn.entrenamientos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Petición para crear o actualizar una rutina de entrenamiento")
public class RutinaRequestDTO {

    @NotNull
    @Schema(description = "ID del atleta (owner) que utilizará la rutina", example = "1")
    private Long ownerId;

    @NotNull
    @Schema(description = "ID del creador de la rutina (por ejemplo, el entrenador)", example = "10")
    private Long creatorId;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nombre de la rutina", example = "Rutina de Inicio - Full Body")
    private String nombre;

    @Size(max = 255)
    @Schema(description = "Descripción general de la rutina", example = "Sesión full body para principiantes, 3 días por semana.")
    private String descripcion;

    @Valid
    @Schema(description = "Lista de ejercicios que componen la rutina (puede ir vacía en la creación)")
    private List<RutinaEjercicioRequestDTO> ejercicios;
}

