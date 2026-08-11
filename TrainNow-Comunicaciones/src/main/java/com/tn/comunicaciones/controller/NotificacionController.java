package com.tn.comunicaciones.controller;

import com.tn.comunicaciones.dto.NotificationDto;
import com.tn.comunicaciones.service.NotificacionService;
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
    public List<NotificationDto> getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @GetMapping("/{id}")
    public NotificationDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<NotificationDto> create(@Valid @RequestBody NotificationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public NotificationDto update(@PathVariable Long id, @RequestBody NotificationDto dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/read")
    public NotificationDto markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
