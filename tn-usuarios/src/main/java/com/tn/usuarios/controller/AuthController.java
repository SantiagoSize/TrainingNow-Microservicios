package com.tn.usuarios.controller;

import com.tn.usuarios.dto.LoginRequestDTO;
import com.tn.usuarios.dto.LoginResponseDTO;
import com.tn.usuarios.dto.RegisterRequestDTO;
import com.tn.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Identidad y Acceso", description = "Registro de usuarios e inicio de sesión con JWT")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar usuario",
            description = "Crea un nuevo usuario en la plataforma con datos personales, físicos y objetivo. Devuelve token JWT y perfil."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente. Se devuelve el token JWT y los datos del usuario."),
            @ApiResponse(responseCode = "400", description = "Bad Request: datos de validación incorrectos o email ya registrado."),
            @ApiResponse(responseCode = "404", description = "Not Found: recurso no disponible (p. ej. rol inexistente)."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<LoginResponseDTO> register(@Valid @NonNull @RequestBody RegisterRequestDTO request) {
        LoginResponseDTO response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica con email y contraseña. Devuelve token JWT para usar en cabecera Authorization (Bearer <token>)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login correcto. Se devuelve el token JWT y los datos del usuario."),
            @ApiResponse(responseCode = "400", description = "Bad Request: formato de email o contraseña incorrecto."),
            @ApiResponse(responseCode = "401", description = "Unauthorized: credenciales inválidas o usuario desactivado."),
            @ApiResponse(responseCode = "404", description = "Not Found: usuario no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
