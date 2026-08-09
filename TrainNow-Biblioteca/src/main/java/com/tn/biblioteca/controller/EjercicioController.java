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

    @PostMapping
    public ResponseEntity<ExerciseDto> create(@Valid @RequestBody ExerciseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ExerciseDto update(@PathVariable Long id, @RequestBody ExerciseDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
