package com.tn.biblioteca.service;

import com.tn.biblioteca.dto.ExerciseDto;
import com.tn.biblioteca.exception.ResourceNotFoundException;
import com.tn.biblioteca.model.Ejercicio;
import com.tn.biblioteca.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio de la biblioteca de ejercicios. */
@Service
@RequiredArgsConstructor
@Transactional
public class EjercicioService {

    private final EjercicioRepository repository;

    @Transactional(readOnly = true)
    public List<ExerciseDto> getAll() {
        return repository.findAll().stream().map(ExerciseDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ExerciseDto getById(Long id) {
        return ExerciseDto.fromEntity(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ExerciseDto> getByCategory(String category) {
        return repository.findByCategoryIgnoreCase(category).stream().map(ExerciseDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<ExerciseDto> search(String q) {
        if (q == null || q.isBlank()) return getAll();
        return repository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(q, q)
                .stream().map(ExerciseDto::fromEntity).toList();
    }

    public ExerciseDto create(ExerciseDto dto) {
        Ejercicio e = dto.toEntity();
        e.setId(null);
        return ExerciseDto.fromEntity(repository.save(e));
    }

    public ExerciseDto update(Long id, ExerciseDto dto) {
        Ejercicio existing = findOrThrow(id);
        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setDescription(dto.getDescription());
        existing.setVideoUrl(dto.getVideoUrl());
        existing.setImageUrl(dto.getImageUrl());
        existing.setMuscles(dto.getMuscles());
        existing.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty() : existing.getDifficulty());
        existing.setEquipment(dto.getEquipment());
        existing.setInstructions(dto.getInstructions());
        existing.setTips(dto.getTips());
        existing.setCommonMistakes(dto.getCommonMistakes());
        existing.setRecommendedSets(dto.getRecommendedSets() != null ? dto.getRecommendedSets() : existing.getRecommendedSets());
        existing.setRecommendedReps(dto.getRecommendedReps());
        existing.setRestSeconds(dto.getRestSeconds() != null ? dto.getRestSeconds() : existing.getRestSeconds());
        existing.setIsSystemDefault(dto.getIsSystemDefault() != null ? dto.getIsSystemDefault() : existing.getIsSystemDefault());
        return ExerciseDto.fromEntity(repository.save(existing));
    }

    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Ejercicio findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado: id=" + id));
    }
}
