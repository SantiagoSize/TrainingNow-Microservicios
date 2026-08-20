package com.tn.usuarios.controller;

import com.tn.usuarios.dto.AuditLogDto;
import com.tn.usuarios.service.AuditLogService;
import com.tn.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Registro de actividad administrativa. Tanto para guardar como para listar
 * se exige un token JWT de un ADMIN activo: es información sensible sobre
 * lo que hace el personal dentro de la app.
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final UserService userService;

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro de actividad guardado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo")
    })
    public ResponseEntity<AuditLogDto> record(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AuditLogDto dto) {
        userService.requireActiveAdmin(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(auditLogService.record(dto));
    }

    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de actividad (opcionalmente filtrada por targetType)"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo")
    })
    public List<AuditLogDto> getAll(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String targetType) {
        userService.requireActiveAdmin(authHeader);
        if (targetType != null && !targetType.isBlank()) {
            return auditLogService.getByTargetType(targetType);
        }
        return auditLogService.getAll();
    }
}
