package com.tn.rutinas.repository;

import com.tn.rutinas.model.EjercicioRutina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjercicioRutinaRepository extends JpaRepository<EjercicioRutina, Long> {

    List<EjercicioRutina> findByRoutineIdOrderByOrderIndexAsc(Long routineId);

    void deleteByRoutineId(Long routineId);
}
