package com.tn.rutinas.controller;

import com.tn.rutinas.dto.RoutineDto;
import com.tn.rutinas.dto.RoutineExerciseDto;
import com.tn.rutinas.service.RutinaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponse(responseCode = "200", description = "Lista de rutinas")
    public List<RoutineDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/public")
    @ApiResponse(responseCode = "200", description = "Rutinas globales/recomendadas (ownerId = null)")
    public List<RoutineDto> getPublic() {
        return service.getPublic();
    }

    @GetMapping("/owner/{ownerId}")
    @ApiResponse(responseCode = "200", description = "Rutinas de ese dueño")
    public List<RoutineDto> getByOwner(@PathVariable Long ownerId) {
        return service.getByOwner(ownerId);
    }

    @GetMapping("/creator/{creatorId}")
    @ApiResponse(responseCode = "200", description = "Rutinas creadas por ese entrenador/admin")
    public List<RoutineDto> getByCreator(@PathVariable Long creatorId) {
        return service.getByCreator(creatorId);
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rutina encontrada"),
            @ApiResponse(responseCode = "404", description = "La rutina no existe")
    })
    public RoutineDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/{id}/exercises")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejercicios de la rutina"),
            @ApiResponse(responseCode = "404", description = "La rutina no existe")
    })
    public List<RoutineExerciseDto> getExercises(@PathVariable Long id) {
        return service.getExercises(id);
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rutina creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<RoutineDto> create(@Valid @RequestBody RoutineDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PostMapping("/{id}/exercises")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejercicios de la rutina reemplazados"),
            @ApiResponse(responseCode = "404", description = "La rutina no existe")
    })
    public ResponseEntity<Void> setExercises(@PathVariable Long id,
                                             @RequestBody List<RoutineExerciseDto> exercises) {
        service.setExercises(id, exercises);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rutina actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "La rutina no existe")
    })
    public RoutineDto update(@PathVariable Long id, @RequestBody RoutineDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rutina eliminada"),
            @ApiResponse(responseCode = "404", description = "La rutina no existe")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
