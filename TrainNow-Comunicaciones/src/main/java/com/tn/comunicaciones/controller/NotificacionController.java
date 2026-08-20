package com.tn.comunicaciones.controller;

import com.tn.comunicaciones.dto.NotificationDto;
import com.tn.comunicaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de notificaciones. Contrato consumido por NotificationApi.kt (Android).
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService service;

    @GetMapping("/user/{userId}")
    @ApiResponse(responseCode = "200", description = "Notificaciones del usuario")
    public List<NotificationDto> getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
            @ApiResponse(responseCode = "404", description = "La notificación no existe")
    })
    public NotificationDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificación creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<NotificationDto> create(@Valid @RequestBody NotificationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación actualizada"),
            @ApiResponse(responseCode = "404", description = "La notificación no existe")
    })
    public NotificationDto update(@PathVariable Long id, @RequestBody NotificationDto dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/read")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída"),
            @ApiResponse(responseCode = "404", description = "La notificación no existe")
    })
    public NotificationDto markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notificación eliminada"),
            @ApiResponse(responseCode = "404", description = "La notificación no existe")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
