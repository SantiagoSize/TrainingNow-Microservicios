package com.tn.entrenamientos.controller;

import com.tn.entrenamientos.dto.RutinaDTO;
import com.tn.entrenamientos.dto.RutinaEjercicioDTO;
import com.tn.entrenamientos.dto.RutinaEjercicioRequestDTO;
import com.tn.entrenamientos.dto.RutinaRequestDTO;
import com.tn.entrenamientos.service.RutinaService;
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
@RequestMapping("/api/routines")
@Tag(name = "Routine Service", description = "Gestión de rutinas de entrenamiento (ROUTINE-SERVICE)")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;

    @PostMapping
    @Operation(summary = "Crear una nueva rutina")
    public ResponseEntity<RutinaDTO> crearRutina(@Valid @RequestBody RutinaRequestDTO request) {
        RutinaDTO creada = rutinaService.crearRutina(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "GET /api/routines/owner/{ownerId} - Rutinas de un atleta")
    public ResponseEntity<List<RutinaDTO>> obtenerRutinasPorOwner(@PathVariable Long ownerId) {
        List<RutinaDTO> rutinas = rutinaService.obtenerRutinasPorOwner(ownerId);
        return ResponseEntity.ok(rutinas);
    }

    @GetMapping("/creator/{creatorId}")
    @Operation(summary = "GET /api/routines/creator/{creatorId} - Rutinas creadas por un coach")
    public ResponseEntity<List<RutinaDTO>> obtenerRutinasPorCreator(@PathVariable Long creatorId) {
        List<RutinaDTO> rutinas = rutinaService.obtenerRutinasPorCreator(creatorId);
        return ResponseEntity.ok(rutinas);
    }

    @PostMapping("/{id}/exercises")
    @Operation(summary = "POST /api/routines/{id}/exercises - Asignar ejercicios a una rutina existente")
    public ResponseEntity<List<RutinaEjercicioDTO>> asignarEjercicios(
            @PathVariable Long id,
            @Valid @RequestBody List<RutinaEjercicioRequestDTO> ejercicios) {
        List<RutinaEjercicioDTO> result = rutinaService.asignarEjercicios(id, ejercicios);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{id}/exercises")
    @Operation(summary = "GET /api/routines/{id}/exercises - Ver ejercicios de una rutina")
    public ResponseEntity<List<RutinaEjercicioDTO>> obtenerEjercicios(@PathVariable Long id) {
        List<RutinaEjercicioDTO> ejercicios = rutinaService.obtenerEjerciciosPorRutina(id);
        return ResponseEntity.ok(ejercicios);
    }
}

