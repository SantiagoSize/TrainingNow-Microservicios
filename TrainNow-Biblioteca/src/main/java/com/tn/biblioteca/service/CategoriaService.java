package com.tn.biblioteca.service;

import com.tn.biblioteca.dto.CategoriaDto;
import com.tn.biblioteca.exception.ForbiddenOperationException;
import com.tn.biblioteca.model.Categoria;
import com.tn.biblioteca.repository.CategoriaRepository;
import com.tn.biblioteca.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Categorías de la biblioteca. Une las categorías creadas explícitamente (tabla "categories")
 * con las que ya existían implícitamente por tener ejercicios (compatibilidad con datos previos
 * a esta tabla), para que ninguna categoría real se "pierda" de la lista.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final EjercicioRepository ejercicioRepository;

    @Transactional(readOnly = true)
    public List<CategoriaDto> getAll() {
        Map<String, CategoriaDto> porNombre = new LinkedHashMap<>();

        for (Categoria c : categoriaRepository.findAllByOrderByNameAsc()) {
            long count = ejercicioRepository.countByCategoryIgnoreCase(c.getName());
            porNombre.put(c.getName().toLowerCase(), CategoriaDto.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .exerciseCount(count)
                    .build());
        }

        // Compatibilidad: categorías que ya tenían ejercicios antes de existir esta tabla.
        for (String nombre : ejercicioRepository.findDistinctCategories()) {
            if (nombre == null || nombre.isBlank()) continue;
            porNombre.computeIfAbsent(nombre.toLowerCase(), k -> CategoriaDto.builder()
                    .id(null)
                    .name(nombre)
                    .exerciseCount(ejercicioRepository.countByCategoryIgnoreCase(nombre))
                    .build());
        }

        return porNombre.values().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList();
    }

    public CategoriaDto create(String name) {
        if (name == null || name.isBlank()) {
            throw new ForbiddenOperationException("El nombre de la categoría es obligatorio");
        }
        String limpio = name.trim();
        boolean yaExiste = categoriaRepository.findByNameIgnoreCase(limpio).isPresent()
                || ejercicioRepository.findDistinctCategories().stream()
                    .anyMatch(c -> c != null && c.equalsIgnoreCase(limpio));
        if (yaExiste) {
            throw new ForbiddenOperationException("Ya existe una categoría con ese nombre");
        }
        Categoria guardada = categoriaRepository.save(Categoria.builder().name(limpio).build());
        return CategoriaDto.builder().id(guardada.getId()).name(guardada.getName()).exerciseCount(0).build();
    }

    /** Renombra la categoría (fila propia, si existe) y todos los ejercicios que la usan. */
    public CategoriaDto rename(String oldName, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new ForbiddenOperationException("El nuevo nombre es obligatorio");
        }
        String limpio = newName.trim();
        if (!limpio.equalsIgnoreCase(oldName)) {
            boolean colisiona = categoriaRepository.findByNameIgnoreCase(limpio).isPresent()
                    || !ejercicioRepository.findByCategoryIgnoreCase(limpio).isEmpty();
            if (colisiona) {
                throw new ForbiddenOperationException("Ya existe una categoría con ese nombre");
            }
        }

        Categoria categoria = categoriaRepository.findByNameIgnoreCase(oldName)
                .map(c -> { c.setName(limpio); return categoriaRepository.save(c); })
                .orElseGet(() -> categoriaRepository.save(Categoria.builder().name(limpio).build()));

        ejercicioRepository.findByCategoryIgnoreCase(oldName).forEach(e -> {
            e.setCategory(limpio);
            ejercicioRepository.save(e);
        });

        long count = ejercicioRepository.countByCategoryIgnoreCase(limpio);
        return CategoriaDto.builder().id(categoria.getId()).name(categoria.getName()).exerciseCount(count).build();
    }

    /** Elimina la categoría y todos los ejercicios que contiene. */
    public void delete(String name) {
        categoriaRepository.findByNameIgnoreCase(name).ifPresent(categoriaRepository::delete);
        ejercicioRepository.deleteByCategoryIgnoreCase(name);
    }
}
