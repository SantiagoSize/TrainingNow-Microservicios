package com.tn.rutinas.controller;

import com.tn.rutinas.dto.ExerciseLogDto;
import com.tn.rutinas.dto.WorkoutSessionDto;
import com.tn.rutinas.service.WorkoutService;
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
    public List<WorkoutSessionDto> getSessionsByUser(@PathVariable Long userId) {
        return service.getSessionsByUser(userId);
    }

    @GetMapping("/sessions/user/{userId}/status/{status}")
    public List<WorkoutSessionDto> getSessionsByUserAndStatus(@PathVariable Long userId,
                                                              @PathVariable String status) {
        return service.getSessionsByUserAndStatus(userId, status);
    }

    @GetMapping("/sessions/{id}")
    public WorkoutSessionDto getSessionById(@PathVariable Long id) {
        return service.getSessionById(id);
    }

    @GetMapping("/sessions/{sessionId}/logs")
    public List<ExerciseLogDto> getLogsBySession(@PathVariable Long sessionId) {
        return service.getLogsBySession(sessionId);
    }

    @PostMapping("/sessions")
    public ResponseEntity<WorkoutSessionDto> createSession(@Valid @RequestBody WorkoutSessionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSession(dto));
    }

    @PutMapping("/sessions/{id}")
    public WorkoutSessionDto updateSession(@PathVariable Long id, @RequestBody WorkoutSessionDto dto) {
        return service.updateSession(id, dto);
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        service.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logs")
    public ResponseEntity<ExerciseLogDto> createLog(@Valid @RequestBody ExerciseLogDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLog(dto));
    }
}
