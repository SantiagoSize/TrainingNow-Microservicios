package com.tn.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Datos para iniciar sesión en la plataforma")
public class LoginRequestDTO {

    @Schema(
            description = "Email del usuario (identificador de acceso)",
            example = "usuario@trainingnow.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @Schema(
            description = "Contraseña del usuario",
            example = "MiClaveSegura123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
