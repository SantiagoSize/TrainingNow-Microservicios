package com.tn.biblioteca.controller;

import com.tn.biblioteca.dto.ExerciseDto;
import com.tn.biblioteca.service.EjercicioService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponse(responseCode = "200", description = "Lista de ejercicios")
    public List<ExerciseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/search")
    @ApiResponse(responseCode = "200", description = "Ejercicios que calzan con la búsqueda")
    public List<ExerciseDto> search(@RequestParam(name = "q", required = false) String q) {
        return service.search(q);
    }

    @GetMapping("/category/{category}")
    @ApiResponse(responseCode = "200", description = "Ejercicios de esa categoría")
    public List<ExerciseDto> getByCategory(@PathVariable String category) {
        return service.getByCategory(category);
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejercicio encontrado"),
            @ApiResponse(responseCode = "404", description = "El ejercicio no existe")
    })
    public ExerciseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // ===== Escritura: solo administradores (token JWT de TrainNow-Usuarios) =====

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ejercicio creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo")
    })
    public ResponseEntity<ExerciseDto> create(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ExerciseDto dto) {
        jwtValidator.requireAdmin(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejercicio actualizado"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "404", description = "El ejercicio no existe")
    })
    public ExerciseDto update(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody ExerciseDto dto) {
        jwtValidator.requireAdmin(authHeader);
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ejercicio eliminado"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "404", description = "El ejercicio no existe")
    })
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        jwtValidator.requireAdmin(authHeader);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
