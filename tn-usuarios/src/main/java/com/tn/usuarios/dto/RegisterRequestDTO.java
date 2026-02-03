package com.tn.usuarios.dto;

import com.tn.usuarios.model.Genero;
import com.tn.usuarios.model.Objetivo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Datos para registrar un nuevo atleta o usuario en la plataforma")
public class RegisterRequestDTO {

    @Schema(
            description = "Email del usuario (será el identificador de acceso)",
            example = "atleta@trainingnow.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @Schema(
            description = "Contraseña (se almacenará encriptada con BCrypt)",
            example = "MiClaveSegura123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @Schema(
            description = "Nombre del usuario",
            example = "Carlos",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(
            description = "Apellidos del usuario",
            example = "Martínez López",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String apellidos;

    @Schema(
            description = "Teléfono de contacto",
            example = "+34 612 345 678",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String telefono;

    @Schema(
            description = "Fecha de nacimiento (para perfiles de entrenamiento)",
            example = "1992-08-15",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDate fechaNacimiento;

    @Schema(
            description = "Género (MASCULINO, FEMENINO, OTRO)",
            example = "MASCULINO",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Genero genero;

    @Schema(
            description = "Peso actual en kilogramos (dato biométrico)",
            example = "80.5",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Double pesoActual;

    @Schema(
            description = "Altura en centímetros (dato biométrico)",
            example = "180",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Integer altura;

    @Schema(
            description = "Objetivo de entrenamiento (PERDIDA_PESO, GANAR_MUSCULO, MANTENER, DEFINICION, etc.)",
            example = "GANAR_MUSCULO",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Objetivo objetivo;

    @Schema(
            description = "Rol del usuario (ADMIN, TRAINER, CLIENT)",
            example = "CLIENT",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "El rol es obligatorio")
    private String rol;
}
