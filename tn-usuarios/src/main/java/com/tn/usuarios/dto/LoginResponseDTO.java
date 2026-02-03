package com.tn.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de login o registro: token JWT y datos del usuario")
public class LoginResponseDTO {

    @Schema(
            description = "Token JWT para autenticar peticiones (incluir en cabecera Authorization: Bearer <token>)",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String token;

    @Schema(
            description = "Tipo de token (siempre Bearer para JWT)",
            example = "Bearer",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String type = "Bearer";

    @Schema(
            description = "Datos del usuario autenticado (perfil sin contraseña)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UserDTO user;
}
