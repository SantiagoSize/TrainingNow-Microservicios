package com.tn.entrenamientos.controller;

import com.tn.entrenamientos.dto.SesionEntrenamientoDTO;
import com.tn.entrenamientos.dto.SesionEntrenamientoRequestDTO;
import com.tn.entrenamientos.service.SesionEntrenamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
@Tag(name = "Workout Tracking", description = "Registro de sesiones de entrenamiento completadas")
@RequiredArgsConstructor
public class SesionEntrenamientoController {

    private final SesionEntrenamientoService sesionEntrenamientoService;

    @PostMapping
    @Operation(summary = "Registrar una sesión completada")
    public ResponseEntity<SesionEntrenamientoDTO> registrarSesion(
            @Valid @RequestBody SesionEntrenamientoRequestDTO request) {
        SesionEntrenamientoDTO creada = sesionEntrenamientoService.registrarSesion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar sesiones de entrenamiento de un usuario")
    public ResponseEntity<List<SesionEntrenamientoDTO>> obtenerSesionesPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(sesionEntrenamientoService.obtenerSesionesPorUsuario(userId));
    }
}

