package com.tn.biblioteca.controller;

import com.tn.biblioteca.dto.EjercicioDTO;
import com.tn.biblioteca.dto.EjercicioRequestDTO;
import com.tn.biblioteca.model.Dificultad;
import com.tn.biblioteca.model.GrupoMuscular;
import com.tn.biblioteca.service.EjercicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Tag(name = "Biblioteca de Ejercicios", description = "CRUD de ejercicios de la biblioteca de entrenamiento")
public class EjercicioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EjercicioController.class);

    private final EjercicioService ejercicioService;

    @GetMapping
    @Operation(
            summary = "Listar ejercicios",
            description = "Devuelve la lista completa de ejercicios o los filtra por grupo muscular si se indica el parámetro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ejercicios devuelta correctamente."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<List<EjercicioDTO>> findAll(
            @Parameter(description = "Grupo muscular por el que filtrar (opcional)")
            @RequestParam(name = "grupoMuscular", required = false) GrupoMuscular grupoMuscular
    ) {
        if (grupoMuscular != null) {
            LOGGER.info("GET /api/exercises?grupoMuscular={}", grupoMuscular);
            return ResponseEntity.ok(ejercicioService.findByGrupoMuscular(grupoMuscular));
        }
        LOGGER.info("GET /api/exercises");
        return ResponseEntity.ok(ejercicioService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ejercicio por ID", description = "Devuelve un ejercicio por su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ejercicio encontrado."),
            @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<EjercicioDTO> findById(
            @Parameter(description = "ID del ejercicio") @PathVariable @NonNull Long id) {
        LOGGER.info("GET /api/exercises/{}", id);
        return ResponseEntity.ok(ejercicioService.findById(id));
    }

    @GetMapping("/grupo/{grupoMuscular}")
    @Operation(summary = "Listar por grupo muscular", description = "Devuelve ejercicios filtrados por grupo muscular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ejercicios devuelta correctamente."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<List<EjercicioDTO>> findByGrupoMuscular(
            @Parameter(description = "Grupo muscular (PECHO, ESPALDA, PIERNAS, HOMBROS, BRAZOS, CORE)") @PathVariable @NonNull GrupoMuscular grupoMuscular) {
        LOGGER.info("GET /api/exercises/grupo/{}", grupoMuscular);
        return ResponseEntity.ok(ejercicioService.findByGrupoMuscular(grupoMuscular));
    }

    @GetMapping("/dificultad/{dificultad}")
    @Operation(summary = "Listar por dificultad", description = "Devuelve ejercicios filtrados por nivel de dificultad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ejercicios devuelta correctamente."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<List<EjercicioDTO>> findByDificultad(
            @Parameter(description = "Dificultad (PRINCIPIANTE, INTERMEDIO, AVANZADO)") @PathVariable @NonNull Dificultad dificultad) {
        LOGGER.info("GET /api/exercises/dificultad/{}", dificultad);
        return ResponseEntity.ok(ejercicioService.findByDificultad(dificultad));
    }

    @PostMapping
    @Operation(summary = "Crear ejercicio", description = "Crea un nuevo ejercicio en la biblioteca.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ejercicio creado correctamente."),
            @ApiResponse(responseCode = "400", description = "Datos de validación incorrectos."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<EjercicioDTO> create(@Valid @RequestBody EjercicioRequestDTO request) {
        LOGGER.info("POST /api/exercises nombre={}", request.getNombre());
        EjercicioDTO created = ejercicioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ejercicio", description = "Actualiza un ejercicio existente por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ejercicio actualizado correctamente."),
            @ApiResponse(responseCode = "400", description = "Datos de validación incorrectos."),
            @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<EjercicioDTO> update(
            @Parameter(description = "ID del ejercicio") @PathVariable @NonNull Long id,
            @Valid @RequestBody EjercicioRequestDTO request) {
        LOGGER.info("PUT /api/exercises/{}", id);
        return ResponseEntity.ok(ejercicioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ejercicio", description = "Elimina un ejercicio por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ejercicio eliminado correctamente."),
            @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID del ejercicio") @PathVariable @NonNull Long id) {
        LOGGER.info("DELETE /api/exercises/{}", id);
        ejercicioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
