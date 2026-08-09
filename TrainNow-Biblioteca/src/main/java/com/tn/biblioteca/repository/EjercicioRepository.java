package com.tn.biblioteca.repository;

import com.tn.biblioteca.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    List<Ejercicio> findByCategoryIgnoreCase(String category);

    List<Ejercicio> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);
}
