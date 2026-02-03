package com.tn.biblioteca.repository;

import com.tn.biblioteca.model.Ejercicio;
import com.tn.biblioteca.model.Dificultad;
import com.tn.biblioteca.model.Equipamiento;
import com.tn.biblioteca.model.GrupoMuscular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    List<Ejercicio> findByGrupoMuscular(GrupoMuscular grupoMuscular);

    List<Ejercicio> findByDificultad(Dificultad dificultad);

    List<Ejercicio> findByEquipamiento(Equipamiento equipamiento);
}
