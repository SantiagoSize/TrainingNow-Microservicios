package com.tn.usuarios.controller;

import com.tn.usuarios.dto.PasswordResetConfirm;
import com.tn.usuarios.dto.PasswordResetRequest;
import com.tn.usuarios.dto.PasswordResetVerify;
import com.tn.usuarios.service.PasswordResetService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API de recuperación de contraseña. Flujo: request → verify → confirm.
 */
@RestController
@RequestMapping("/api/users/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService service;

    @PostMapping("/request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código enviado al correo"),
            @ApiResponse(responseCode = "400", description = "Correo inválido"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese correo")
    })
    public Map<String, String> request(@Valid @RequestBody PasswordResetRequest body) {
        service.requestCode(body.getEmail());
        return Map.of("message", "Código enviado al correo");
    }

    @PostMapping("/verify")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código válido"),
            @ApiResponse(responseCode = "400", description = "Código incorrecto, vencido o correo inválido")
    })
    public Map<String, String> verify(@Valid @RequestBody PasswordResetVerify body) {
        service.verifyCode(body.getEmail(), body.getCode());
        return Map.of("message", "Código válido");
    }

    @PostMapping("/confirm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
            @ApiResponse(responseCode = "400", description = "Código inválido o nueva contraseña no cumple la validación")
    })
    public Map<String, String> confirm(@Valid @RequestBody PasswordResetConfirm body) {
        service.confirmReset(body.getEmail(), body.getCode(), body.getNewPassword());
        return Map.of("message", "Contraseña actualizada");
    }
}
