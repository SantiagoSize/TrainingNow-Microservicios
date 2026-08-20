package com.tn.biblioteca.controller;

import com.tn.biblioteca.dto.CategoriaDto;
import com.tn.biblioteca.security.JwtValidator;
import com.tn.biblioteca.service.CategoriaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API REST de categorías de la biblioteca. Contrato consumido por ExerciseApi.kt (Android).
 * Separada de /api/exercises para que una categoría pueda existir sin ejercicios todavía.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;
    private final JwtValidator jwtValidator;

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Lista de categorías")
    public List<CategoriaDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre")
    })
    public ResponseEntity<CategoriaDto> create(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> body) {
        jwtValidator.requireAdmin(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body.get("name")));
    }

    /** Renombra la categoría y actualiza todos los ejercicios que la usan. */
    @PutMapping("/{oldName}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría renombrada"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "404", description = "La categoría no existe")
    })
    public CategoriaDto rename(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String oldName,
            @RequestBody Map<String, String> body) {
        jwtValidator.requireAdmin(authHeader);
        return service.rename(oldName, body.get("name"));
    }

    /** Elimina la categoría y todos los ejercicios que contiene. */
    @DeleteMapping("/{name}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría (y sus ejercicios) eliminados"),
            @ApiResponse(responseCode = "403", description = "El token no pertenece a un admin activo"),
            @ApiResponse(responseCode = "404", description = "La categoría no existe")
    })
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String name) {
        jwtValidator.requireAdmin(authHeader);
        service.delete(name);
        return ResponseEntity.noContent().build();
    }
}
