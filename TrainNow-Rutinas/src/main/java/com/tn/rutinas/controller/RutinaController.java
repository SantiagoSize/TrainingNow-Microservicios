package com.tn.rutinas.controller;

import com.tn.rutinas.dto.RoutineDto;
import com.tn.rutinas.dto.RoutineExerciseDto;
import com.tn.rutinas.service.RutinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de rutinas. Contrato consumido por RoutineApi.kt (Android).
 */
@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService service;

    @GetMapping
    public List<RoutineDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/public")
    public List<RoutineDto> getPublic() {
        return service.getPublic();
    }

    @GetMapping("/owner/{ownerId}")
    public List<RoutineDto> getByOwner(@PathVariable Long ownerId) {
        return service.getByOwner(ownerId);
    }

    @GetMapping("/creator/{creatorId}")
    public List<RoutineDto> getByCreator(@PathVariable Long creatorId) {
        return service.getByCreator(creatorId);
    }

    @GetMapping("/{id}")
    public RoutineDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/{id}/exercises")
    public List<RoutineExerciseDto> getExercises(@PathVariable Long id) {
        return service.getExercises(id);
    }

    @PostMapping
    public ResponseEntity<RoutineDto> create(@Valid @RequestBody RoutineDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PostMapping("/{id}/exercises")
    public ResponseEntity<Void> setExercises(@PathVariable Long id,
                                             @RequestBody List<RoutineExerciseDto> exercises) {
        service.setExercises(id, exercises);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public RoutineDto update(@PathVariable Long id, @RequestBody RoutineDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
