package com.tn.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Petición de login: {"email": "...", "password": "..."}. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
