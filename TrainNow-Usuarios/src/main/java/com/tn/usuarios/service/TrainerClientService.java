package com.tn.usuarios.service;

import com.tn.usuarios.dto.TrainerClientDto;
import com.tn.usuarios.model.TrainerClient;
import com.tn.usuarios.repository.TrainerClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de relaciones entrenador-cliente.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TrainerClientService {

    private final TrainerClientRepository repository;

    @Transactional(readOnly = true)
    public List<TrainerClientDto> getByTrainer(Long trainerId) {
        return repository.findByTrainerId(trainerId).stream().map(TrainerClientDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<TrainerClientDto> getByTrainerAndStatus(Long trainerId, String status) {
        return repository.findByTrainerIdAndStatusIgnoreCase(trainerId, status)
                .stream().map(TrainerClientDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<TrainerClientDto> getByClient(Long clientId) {
        return repository.findByClientId(clientId).stream().map(TrainerClientDto::fromEntity).toList();
    }

    /** Crea la relación; si ya existe (trainerId+clientId) la actualiza (upsert). */
    public TrainerClientDto create(TrainerClientDto dto) {
        TrainerClient entity = repository
                .findByTrainerIdAndClientId(dto.getTrainerId(), dto.getClientId())
                .map(existing -> {
                    existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
                    existing.setStartDate(dto.getStartDate());
                    existing.setEndDate(dto.getEndDate());
                    existing.setTrainerNotes(dto.getTrainerNotes());
                    existing.setClientGoals(dto.getClientGoals());
                    existing.setSessionPrice(dto.getSessionPrice());
                    existing.setSessionsPerWeek(dto.getSessionsPerWeek() != null ? dto.getSessionsPerWeek() : existing.getSessionsPerWeek());
                    existing.setLastInteractionDate(dto.getLastInteractionDate());
                    return existing;
                })
                .orElseGet(dto::toEntity);
        return TrainerClientDto.fromEntity(repository.save(entity));
    }
}
