package com.tn.biblioteca.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Grupo muscular principal que trabaja el ejercicio")
public enum GrupoMuscular {
    PECHO,
    ESPALDA,
    PIERNAS,
    HOMBROS,
    BRAZOS,
    CORE
}
