package com.tn.usuarios.dto;

import com.tn.usuarios.model.Genero;
import com.tn.usuarios.model.Objetivo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Perfil del atleta (sin contraseña). Datos personales y biométricos para el entrenador.")
public class UserDTO {

    @Schema(description = "Identificador único del usuario", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "Email del usuario (identificador de acceso)", example = "atleta@trainingnow.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Nombre del usuario", example = "Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Apellidos del usuario", example = "Martínez López", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String apellidos;

    @Schema(description = "Teléfono de contacto", example = "+34 612 345 678", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String telefono;

    @Schema(description = "Fecha de nacimiento del atleta", example = "1992-08-15", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate fechaNacimiento;

    @Schema(description = "Género (MASCULINO, FEMENINO, OTRO)", example = "MASCULINO", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Genero genero;

    @Schema(description = "Peso actual en kilogramos (dato biométrico)", example = "80.5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Double pesoActual;

    @Schema(description = "Altura en centímetros (dato biométrico)", example = "180", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer altura;

    @Schema(description = "Objetivo de entrenamiento (PERDIDA_PESO, GANAR_MUSCULO, MANTENER, DEFINICION, etc.)", example = "GANAR_MUSCULO", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Objetivo objetivo;

    @Schema(description = "Rol en la plataforma (ADMIN, TRAINER, CLIENT)", example = "CLIENT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rol;

    @Schema(description = "Indica si la cuenta está activa", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean activo;

    @Schema(description = "Fecha de alta en la plataforma", example = "2025-01-15T10:30:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;
}
