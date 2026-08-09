package com.tn.rutinas.repository;

import com.tn.rutinas.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    List<Rutina> findByOwnerId(Long ownerId);

    List<Rutina> findByCreatorId(Long creatorId);

    List<Rutina> findByOwnerIdIsNull();
}
