package com.tn.usuarios.controller;

import com.tn.usuarios.dto.UserDTO;
import com.tn.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Perfil de Usuario", description = "Consulta del perfil del usuario autenticado (datos personales, físicos y objetivo)")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Mi perfil",
            description = "Obtiene el perfil completo del usuario autenticado. Requiere JWT en cabecera Authorization (Bearer <token>)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil del usuario devuelto correctamente."),
            @ApiResponse(responseCode = "401", description = "Unauthorized: token JWT inválido, expirado o no enviado."),
            @ApiResponse(responseCode = "404", description = "Not Found: usuario no encontrado (sesión inválida)."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        UserDTO user = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(user);
    }
}
