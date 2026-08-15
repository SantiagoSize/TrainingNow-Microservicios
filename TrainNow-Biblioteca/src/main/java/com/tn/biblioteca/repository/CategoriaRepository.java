package com.tn.biblioteca.repository;

import com.tn.biblioteca.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNameIgnoreCase(String name);

    List<Categoria> findAllByOrderByNameAsc();
}
