package com.tn.usuarios.controller;

import com.tn.usuarios.dto.LoginRequest;
import com.tn.usuarios.dto.UserDto;
import com.tn.usuarios.service.UserService;
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
    public List<UserDto> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/trainers")
    public List<UserDto> getTrainers() {
        return userService.getTrainers();
    }

    @GetMapping("/trainers/search")
    public List<UserDto> searchTrainers(@RequestParam(name = "q", required = false) String q) {
        return userService.searchTrainers(q);
    }

    @GetMapping("/clients")
    public List<UserDto> getClients() {
        return userService.getClients();
    }

    @GetMapping("/clients/search")
    public List<UserDto> searchClients(@RequestParam(name = "q", required = false) String q) {
        return userService.searchClients(q);
    }

    @GetMapping("/email/{email}")
    public UserDto getByEmail(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    /** Creación de usuarios con privilegios: requiere token JWT de un ADMIN activo. */
    @PostMapping("/admin-create")
    public ResponseEntity<UserDto> createByAdmin(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UserDto dto) {
        Long adminId = userService.requireActiveAdmin(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createByAdmin(adminId, dto));
    }

    // ==================== Sanciones (solo admin con token) ====================

    @PatchMapping("/{id}/ban")
    public UserDto ban(@RequestHeader(value = "Authorization", required = false) String authHeader,
                       @PathVariable Long id,
                       @RequestBody Map<String, String> body) {
        userService.requireActiveAdmin(authHeader);
        return userService.banUser(id, body.getOrDefault("reason", "Sin motivo especificado"));
    }

    @PatchMapping("/{id}/unban")
    public UserDto unban(@RequestHeader(value = "Authorization", required = false) String authHeader,
                         @PathVariable Long id) {
        userService.requireActiveAdmin(authHeader);
        return userService.unbanUser(id);
    }

    @PatchMapping("/{id}/suspend")
    public UserDto suspend(@RequestHeader(value = "Authorization", required = false) String authHeader,
                           @PathVariable Long id,
                           @RequestBody Map<String, Object> body) {
        userService.requireActiveAdmin(authHeader);
        Long until = body.get("untilMillis") instanceof Number n ? n.longValue() : null;
        String reason = body.get("reason") instanceof String r ? r : "Sin motivo especificado";
        return userService.suspendUser(id, until, reason);
    }

    @PatchMapping("/{id}/unsuspend")
    public UserDto unsuspend(@RequestHeader(value = "Authorization", required = false) String authHeader,
                             @PathVariable Long id) {
        userService.requireActiveAdmin(authHeader);
        return userService.unsuspendUser(id);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
