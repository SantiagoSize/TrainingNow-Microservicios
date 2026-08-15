package com.tn.biblioteca.controller;

import com.tn.biblioteca.dto.CategoriaDto;
import com.tn.biblioteca.security.JwtValidator;
import com.tn.biblioteca.service.CategoriaService;
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
    public List<CategoriaDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<CategoriaDto> create(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> body) {
        jwtValidator.requireAdmin(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body.get("name")));
    }

    /** Renombra la categoría y actualiza todos los ejercicios que la usan. */
    @PutMapping("/{oldName}")
    public CategoriaDto rename(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String oldName,
            @RequestBody Map<String, String> body) {
        jwtValidator.requireAdmin(authHeader);
        return service.rename(oldName, body.get("name"));
    }

    /** Elimina la categoría y todos los ejercicios que contiene. */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String name) {
        jwtValidator.requireAdmin(authHeader);
        service.delete(name);
        return ResponseEntity.noContent().build();
    }
}
