package com.tn.usuarios.controller;

import com.tn.usuarios.model.Notificacion;
import com.tn.usuarios.repository.NotificacionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones", description = "Comunicación sencilla entre entrenadores y atletas")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;

    @GetMapping("/user/{usuarioId}")
    @Operation(summary = "Listar notificaciones de un usuario")
    @SuppressWarnings("null")
    public ResponseEntity<List<Notificacion>> findByUsuario(@PathVariable @NonNull Long usuarioId) {
        return ResponseEntity.ok(notificacionRepository.findByUsuarioIdOrderByIdDesc(usuarioId));
    }

    @PostMapping
    @Operation(summary = "Crear una notificación dirigida a un usuario")
    @SuppressWarnings("null")
    public ResponseEntity<Notificacion> create(@RequestBody CreateNotificacionRequest request) {
        Notificacion notificacion = Notificacion.builder()
                .usuarioId(request.usuarioId())
                .mensaje(request.mensaje())
                .leido(false)
                .createdAt(LocalDateTime.now())
                .build();
        Notificacion saved = Objects.requireNonNull(
                notificacionRepository.save(notificacion),
                "saved notification must not be null"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    public record CreateNotificacionRequest(
            @NotNull Long usuarioId,
            @NotBlank String mensaje
    ) {
    }
}
