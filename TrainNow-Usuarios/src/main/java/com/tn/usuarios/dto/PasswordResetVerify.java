package com.tn.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Paso 2: verificar código. {"email": "...", "code": "123456"} */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetVerify {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El código es obligatorio")
    private String code;
}
