package com.tn.biblioteca.repository;

import com.tn.biblioteca.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    List<Ejercicio> findByCategoryIgnoreCase(String category);

    List<Ejercicio> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);

    /** Categorías que ya tienen al menos un ejercicio (compatibilidad con datos existentes). */
    @Query("select distinct e.category from Ejercicio e")
    List<String> findDistinctCategories();

    long countByCategoryIgnoreCase(String category);

    void deleteByCategoryIgnoreCase(String category);
}
