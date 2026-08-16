package com.tn.usuarios.controller;

import com.tn.usuarios.dto.ReportDto;
import com.tn.usuarios.service.ReportService;
import com.tn.usuarios.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Reportes de usuarios. Crear un reporte es una acción de cualquier usuario logueado
 * (ej: reportar a alguien desde el chat); listar y resolver reportes es solo para admin.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ReportDto> create(@Valid @RequestBody ReportDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.create(dto));
    }

    @GetMapping
    public List<ReportDto> getAll(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String status) {
        userService.requireActiveAdmin(authHeader);
        return reportService.getAll(status);
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ReportDto> resolve(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        userService.requireActiveAdmin(authHeader);
        String status = body.getOrDefault("status", "DISMISSED");
        return ResponseEntity.ok(reportService.resolve(id, status));
    }
}
