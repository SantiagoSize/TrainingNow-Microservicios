package com.tn.biblioteca.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de equipamiento necesario para el ejercicio")
public enum Equipamiento {
    MANCUERNAS,
    BARRA,
    MAQUINA,
    PESO_CORPORAL
}
