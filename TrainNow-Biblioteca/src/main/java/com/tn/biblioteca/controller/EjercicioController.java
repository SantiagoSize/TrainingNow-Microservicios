package com.tn.biblioteca.controller;

import com.tn.biblioteca.dto.ExerciseDto;
import com.tn.biblioteca.service.EjercicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de ejercicios. Contrato consumido por ExerciseApi.kt (Android).
 */
@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class EjercicioController {

    private final EjercicioService service;
    private final com.tn.biblioteca.security.JwtValidator jwtValidator;

    @GetMapping
    public List<ExerciseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/search")
    public List<ExerciseDto> search(@RequestParam(name = "q", required = false) String q) {
        return service.search(q);
    }

    @GetMapping("/category/{category}")
    public List<ExerciseDto> getByCategory(@PathVariable String category) {
        return service.getByCategory(category);
    }

    @GetMapping("/{id}")
    public ExerciseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // ===== Escritura: solo administradores (token JWT de TrainNow-Usuarios) =====

    @PostMapping
    public ResponseEntity<ExerciseDto> create(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ExerciseDto dto) {
        jwtValidator.requireAdmin(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ExerciseDto update(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody ExerciseDto dto) {
        jwtValidator.requireAdmin(authHeader);
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        jwtValidator.requireAdmin(authHeader);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
