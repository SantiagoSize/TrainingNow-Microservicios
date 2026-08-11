package com.tn.biblioteca.repository;

import com.tn.biblioteca.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    List<Ejercicio> findByCategoryIgnoreCase(String category);

    List<Ejercicio> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);
}
