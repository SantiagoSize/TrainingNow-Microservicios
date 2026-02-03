package com.tn.usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objetivo de entrenamiento del usuario (PERDIDA_PESO, GANAR_MUSCULO, MANTENER, DEFINICION, RESISTENCIA, OTRO)")
public enum Objetivo {
    PERDIDA_PESO,
    GANAR_MUSCULO,
    MANTENER,
    DEFINICION,
    RESISTENCIA,
    OTRO
}
