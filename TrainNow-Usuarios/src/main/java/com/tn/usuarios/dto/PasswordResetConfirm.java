package com.tn.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** Paso 3: nueva contraseña. {"email": "...", "code": "...", "newPassword": "..."} */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetConfirm {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El código es obligatorio")
    private String code;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;
}
