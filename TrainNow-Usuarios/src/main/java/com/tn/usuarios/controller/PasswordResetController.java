package com.tn.usuarios.controller;

import com.tn.usuarios.dto.PasswordResetConfirm;
import com.tn.usuarios.dto.PasswordResetRequest;
import com.tn.usuarios.dto.PasswordResetVerify;
import com.tn.usuarios.service.PasswordResetService;
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
    public Map<String, String> request(@Valid @RequestBody PasswordResetRequest body) {
        service.requestCode(body.getEmail());
        return Map.of("message", "Código enviado al correo");
    }

    @PostMapping("/verify")
    public Map<String, String> verify(@Valid @RequestBody PasswordResetVerify body) {
        service.verifyCode(body.getEmail(), body.getCode());
        return Map.of("message", "Código válido");
    }

    @PostMapping("/confirm")
    public Map<String, String> confirm(@Valid @RequestBody PasswordResetConfirm body) {
        service.confirmReset(body.getEmail(), body.getCode(), body.getNewPassword());
        return Map.of("message", "Contraseña actualizada");
    }
}
