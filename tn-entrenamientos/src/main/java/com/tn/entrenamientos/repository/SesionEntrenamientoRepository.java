package com.tn.entrenamientos.repository;

import com.tn.entrenamientos.model.SesionEntrenamiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionEntrenamientoRepository extends JpaRepository<SesionEntrenamiento, Long> {

    List<SesionEntrenamiento> findByUserIdOrderByFechaHoraDesc(Long userId);
}

