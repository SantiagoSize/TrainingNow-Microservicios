package com.tn.usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Género del usuario para perfiles de fitness")
public enum Genero {
    MASCULINO,
    FEMENINO,
    OTRO
}
