package com.tn.entrenamientos.repository;

import com.tn.entrenamientos.model.Rutina;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    List<Rutina> findByOwnerId(Long ownerId);

    List<Rutina> findByCreatorId(Long creatorId);
}
