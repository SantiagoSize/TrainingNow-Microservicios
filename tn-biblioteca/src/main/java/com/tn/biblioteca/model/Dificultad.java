package com.tn.biblioteca.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nivel de dificultad del ejercicio")
public enum Dificultad {
    PRINCIPIANTE,
    INTERMEDIO,
    AVANZADO
}
