package com.tn.usuarios.repository;

import com.tn.usuarios.model.TrainerClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerClientRepository extends JpaRepository<TrainerClient, Long> {

    List<TrainerClient> findByTrainerId(Long trainerId);

    List<TrainerClient> findByTrainerIdAndStatusIgnoreCase(Long trainerId, String status);

    List<TrainerClient> findByClientId(Long clientId);

    Optional<TrainerClient> findByTrainerIdAndClientId(Long trainerId, Long clientId);
}
