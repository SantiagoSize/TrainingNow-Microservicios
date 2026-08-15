package com.tn.rutinas.service;

import com.tn.rutinas.dto.RoutineDto;
import com.tn.rutinas.dto.RoutineExerciseDto;
import com.tn.rutinas.exception.ResourceNotFoundException;
import com.tn.rutinas.model.Rutina;
import com.tn.rutinas.repository.EjercicioRutinaRepository;
import com.tn.rutinas.repository.RutinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio de rutinas y sus ejercicios. */
@Service
@RequiredArgsConstructor
@Transactional
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final EjercicioRutinaRepository ejercicioRutinaRepository;

    @Transactional(readOnly = true)
    public List<RoutineDto> getAll() {
        return rutinaRepository.findAll().stream().map(RoutineDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public RoutineDto getById(Long id) {
        return RoutineDto.fromEntity(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<RoutineDto> getByOwner(Long ownerId) {
        return rutinaRepository.findByOwnerId(ownerId).stream().map(RoutineDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<RoutineDto> getByCreator(Long creatorId) {
        return rutinaRepository.findByCreatorId(creatorId).stream().map(RoutineDto::fromEntity).toList();
    }

    /** Rutinas públicas = sin dueño asignado. */
    @Transactional(readOnly = true)
    public List<RoutineDto> getPublic() {
        return rutinaRepository.findByOwnerIdIsNull().stream().map(RoutineDto::fromEntity).toList();
    }

    public RoutineDto create(RoutineDto dto) {
        Rutina r = dto.toEntity();
        r.setId(null);
        return RoutineDto.fromEntity(rutinaRepository.save(r));
    }

    public RoutineDto update(Long id, RoutineDto dto) {
        Rutina existing = findOrThrow(id);
        existing.setOwnerId(dto.getOwnerId());
        existing.setCreatorId(dto.getCreatorId() != null ? dto.getCreatorId() : existing.getCreatorId());
        existing.setName(dto.getName());
        existing.setDayInfo(dto.getDayInfo());
        existing.setScheduledTime(dto.getScheduledTime());
        if (dto.getPendingShare() != null) existing.setPendingShare(dto.getPendingShare());
        if (dto.getIsTemplate() != null) existing.setIsTemplate(dto.getIsTemplate());
        return RoutineDto.fromEntity(rutinaRepository.save(existing));
    }

    public void delete(Long id) {
        Rutina r = findOrThrow(id);
        ejercicioRutinaRepository.deleteByRoutineId(id);
        rutinaRepository.delete(r);
    }

    @Transactional(readOnly = true)
    public List<RoutineExerciseDto> getExercises(Long routineId) {
        findOrThrow(routineId);
        return ejercicioRutinaRepository.findByRoutineIdOrderByOrderIndexAsc(routineId)
                .stream().map(RoutineExerciseDto::fromEntity).toList();
    }

    /** Reemplaza el set completo de ejercicios de la rutina. */
    public void setExercises(Long routineId, List<RoutineExerciseDto> exercises) {
        findOrThrow(routineId);
        ejercicioRutinaRepository.deleteByRoutineId(routineId);
        List<com.tn.rutinas.model.EjercicioRutina> entities = exercises.stream()
                .map(dto -> {
                    var e = dto.toEntity();
                    e.setRoutineId(routineId);
                    return e;
                })
                .toList();
        ejercicioRutinaRepository.saveAll(entities);
    }

    private Rutina findOrThrow(Long id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada: id=" + id));
    }
}
