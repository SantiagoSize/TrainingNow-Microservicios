package com.tn.usuarios.controller;

import com.tn.usuarios.dto.LoginRequest;
import com.tn.usuarios.dto.UserDto;
import com.tn.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API REST de usuarios. Contrato consumido por UserApi.kt (Android).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    public List<UserDto> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/trainers")
    @ApiResponse(responseCode = "200", description = "Lista de entrenadores")
    public List<UserDto> getTrainers() {
        return userService.getTrainers();
    }

    @GetMapping("/trainers/search")
    @ApiResponse(responseCode = "200", description = "Entrenadores que calzan con la búsqueda")
    public List<UserDto> searchTrainers(@RequestParam(name = "q", required = false) String q) {
        return userService.searchTrainers(q);
    }

    @GetMapping("/clients")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    public List<UserDto> getClients() {
        return userService.getClients();
    }

    @GetMapping("/clients/search")
    @ApiResponse(responseCode = "200", description = "Clientes que calzan con la búsqueda")
    public List<UserDto> searchClients(@RequestParam(name = "q", required = false) String q) {
        return userService.searchClients(q);
    }

    @GetMapping("/email/{email}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese correo")
    })
    public UserDto getByEmail(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id")
    })
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto"),
            @ApiResponse(responseCode = "400", description = "Correo o contraseña con formato inválido"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
            @ApiResponse(responseCode = "403", description = "Cuenta baneada o suspendida")
    })
    public UserDto login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado")
    })
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    /** Creación de usuarios con privilegios: requiere token JWT de un ADMIN activo. */
    @PostMapping("/admin-create")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario de staff creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado")
    })
    public ResponseEntity<UserDto> createByAdmin(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UserDto dto) {
        Long adminId = userService.requireActiveAdmin(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createByAdmin(adminId, dto));
    }

    // ==================== Sanciones (solo admin con token) ====================

    @PatchMapping("/{id}/ban")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario baneado"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public UserDto ban(@RequestHeader(value = "Authorization", required = false) String authHeader,
                       @PathVariable Long id,
                       @RequestBody Map<String, String> body) {
        userService.requireActiveAdmin(authHeader);
        return userService.banUser(id, body.getOrDefault("reason", "Sin motivo especificado"));
    }

    @PatchMapping("/{id}/unban")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Baneo levantado"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public UserDto unban(@RequestHeader(value = "Authorization", required = false) String authHeader,
                         @PathVariable Long id) {
        userService.requireActiveAdmin(authHeader);
        return userService.unbanUser(id);
    }

    @PatchMapping("/{id}/suspend")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario suspendido"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public UserDto suspend(@RequestHeader(value = "Authorization", required = false) String authHeader,
                           @PathVariable Long id,
                           @RequestBody Map<String, Object> body) {
        userService.requireActiveAdmin(authHeader);
        Long until = body.get("untilMillis") instanceof Number n ? n.longValue() : null;
        String reason = body.get("reason") instanceof String r ? r : "Sin motivo especificado";
        return userService.suspendUser(id, until, reason);
    }

    @PatchMapping("/{id}/unsuspend")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suspensión levantada"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public UserDto unsuspend(@RequestHeader(value = "Authorization", required = false) String authHeader,
                             @PathVariable Long id) {
        userService.requireActiveAdmin(authHeader);
        return userService.unsuspendUser(id);
    }

    @PutMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto) {
        return userService.update(id, dto);
    }

    /** Ping de presencia: la app lo llama periódicamente mientras está en primer plano. */
    @PatchMapping("/{id}/heartbeat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Presencia registrada"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> heartbeat(@PathVariable Long id) {
        userService.heartbeat(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "401", description = "Falta el token de autorización"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo, o no se puede eliminar al último administrador"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> delete(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                        @PathVariable Long id) {
        userService.requireActiveAdmin(authHeader);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
