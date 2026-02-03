package com.tn.entrenamientos.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ejercicio obtenido desde el microservicio de biblioteca de ejercicios")
public class EjercicioDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String grupoMuscular;
    private String dificultad;
}

