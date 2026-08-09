package com.tn.rutinas.service;

import com.tn.rutinas.dto.ExerciseLogDto;
import com.tn.rutinas.dto.WorkoutSessionDto;
import com.tn.rutinas.exception.ResourceNotFoundException;
import com.tn.rutinas.model.WorkoutSession;
import com.tn.rutinas.repository.ExerciseLogRepository;
import com.tn.rutinas.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio de sesiones de entrenamiento y logs de ejercicios. */
@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutService {

    private final WorkoutSessionRepository sessionRepository;
    private final ExerciseLogRepository logRepository;

    @Transactional(readOnly = true)
    public WorkoutSessionDto getSessionById(Long id) {
        return WorkoutSessionDto.fromEntity(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<WorkoutSessionDto> getSessionsByUser(Long userId) {
        return sessionRepository.findByUserIdOrderByStartTimeDesc(userId)
                .stream().map(WorkoutSessionDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<WorkoutSessionDto> getSessionsByUserAndStatus(Long userId, String status) {
        return sessionRepository.findByUserIdAndStatusIgnoreCaseOrderByStartTimeDesc(userId, status)
                .stream().map(WorkoutSessionDto::fromEntity).toList();
    }

    public WorkoutSessionDto createSession(WorkoutSessionDto dto) {
        WorkoutSession s = dto.toEntity();
        s.setId(null);
        return WorkoutSessionDto.fromEntity(sessionRepository.save(s));
    }

    public WorkoutSessionDto updateSession(Long id, WorkoutSessionDto dto) {
        WorkoutSession existing = findOrThrow(id);
        existing.setRoutineId(dto.getRoutineId());
        existing.setStartTime(dto.getStartTime() != null ? dto.getStartTime() : existing.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
        existing.setTotalDurationMinutes(dto.getTotalDurationMinutes());
        existing.setCaloriesBurned(dto.getCaloriesBurned());
        existing.setNotes(dto.getNotes());
        existing.setRating(dto.getRating());
        existing.setPerceivedDifficulty(dto.getPerceivedDifficulty());
        existing.setMood(dto.getMood());
        existing.setLocation(dto.getLocation());
        return WorkoutSessionDto.fromEntity(sessionRepository.save(existing));
    }

    public void deleteSession(Long id) {
        WorkoutSession s = findOrThrow(id);
        logRepository.deleteBySessionId(id);
        sessionRepository.delete(s);
    }

    @Transactional(readOnly = true)
    public List<ExerciseLogDto> getLogsBySession(Long sessionId) {
        findOrThrow(sessionId);
        return logRepository.findBySessionIdOrderByOrderInSessionAsc(sessionId)
                .stream().map(ExerciseLogDto::fromEntity).toList();
    }

    public ExerciseLogDto createLog(ExerciseLogDto dto) {
        findOrThrow(dto.getSessionId());
        var log = dto.toEntity();
        log.setId(null);
        return ExerciseLogDto.fromEntity(logRepository.save(log));
    }

    private WorkoutSession findOrThrow(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada: id=" + id));
    }
}
