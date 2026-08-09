package com.tn.rutinas.repository;

import com.tn.rutinas.model.EjercicioRutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRutinaRepository extends JpaRepository<EjercicioRutina, Long> {

    List<EjercicioRutina> findByRoutineIdOrderByOrderIndexAsc(Long routineId);

    void deleteByRoutineId(Long routineId);
}
