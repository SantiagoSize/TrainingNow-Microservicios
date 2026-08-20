package com.tn.rutinas.controller;

import com.tn.rutinas.dto.ExerciseLogDto;
import com.tn.rutinas.dto.WorkoutSessionDto;
import com.tn.rutinas.service.WorkoutService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de sesiones de entrenamiento. Contrato consumido por WorkoutApi.kt (Android).
 */
@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService service;

    @GetMapping("/sessions/user/{userId}")
    @ApiResponse(responseCode = "200", description = "Sesiones de entrenamiento del usuario")
    public List<WorkoutSessionDto> getSessionsByUser(@PathVariable Long userId) {
        return service.getSessionsByUser(userId);
    }

    @GetMapping("/sessions/user/{userId}/status/{status}")
    @ApiResponse(responseCode = "200", description = "Sesiones del usuario filtradas por status")
    public List<WorkoutSessionDto> getSessionsByUserAndStatus(@PathVariable Long userId,
                                                              @PathVariable String status) {
        return service.getSessionsByUserAndStatus(userId, status);
    }

    @GetMapping("/sessions/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión encontrada"),
            @ApiResponse(responseCode = "404", description = "La sesión no existe")
    })
    public WorkoutSessionDto getSessionById(@PathVariable Long id) {
        return service.getSessionById(id);
    }

    @GetMapping("/sessions/{sessionId}/logs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Series registradas en la sesión"),
            @ApiResponse(responseCode = "404", description = "La sesión no existe")
    })
    public List<ExerciseLogDto> getLogsBySession(@PathVariable Long sessionId) {
        return service.getLogsBySession(sessionId);
    }

    @PostMapping("/sessions")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sesión creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<WorkoutSessionDto> createSession(@Valid @RequestBody WorkoutSessionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSession(dto));
    }

    @PutMapping("/sessions/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión actualizada"),
            @ApiResponse(responseCode = "404", description = "La sesión no existe")
    })
    public WorkoutSessionDto updateSession(@PathVariable Long id, @RequestBody WorkoutSessionDto dto) {
        return service.updateSession(id, dto);
    }

    @DeleteMapping("/sessions/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sesión eliminada"),
            @ApiResponse(responseCode = "404", description = "La sesión no existe")
    })
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        service.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logs")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Serie (reps/carga) registrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ExerciseLogDto> createLog(@Valid @RequestBody ExerciseLogDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLog(dto));
    }
}
