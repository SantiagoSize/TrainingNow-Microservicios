package com.tn.rutinas.service;

import com.tn.rutinas.dto.AttendanceDayDto;
import com.tn.rutinas.dto.MonthlyReportDto;
import com.tn.rutinas.model.AttendanceDay;
import com.tn.rutinas.repository.AttendanceDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Registro de asistencia y generación del reporte mensual de entrenamiento.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private static final String TRAINED = "TRAINED";
    private static final String MISSED = "MISSED";
    private static final String REST = "REST";

    private final AttendanceDayRepository repository;

    /** Registra (o actualiza) la asistencia de un día. Idempotente por usuario+fecha. */
    public AttendanceDayDto register(AttendanceDayDto dto) {
        AttendanceDay entity = repository.findByUserIdAndDate(dto.getUserId(), dto.getDate())
                .map(existing -> {
                    existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
                    existing.setRoutineId(dto.getRoutineId());
                    if (dto.getExercisesCompleted() != null) {
                        existing.setExercisesCompleted(dto.getExercisesCompleted());
                    }
                    if (dto.getDurationMinutes() != null) {
                        existing.setDurationMinutes(dto.getDurationMinutes());
                    }
                    return existing;
                })
                .orElseGet(dto::toEntity);
        return AttendanceDayDto.fromEntity(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AttendanceDayDto> getByUser(Long userId) {
        return repository.findByUserIdOrderByDateAsc(userId)
                .stream().map(AttendanceDayDto::fromEntity).toList();
    }

    /**
     * Reporte del mes indicado (formato yyyy-MM): días entrenados, perdidos,
     * de descanso, totales, adherencia y rachas.
     */
    @Transactional(readOnly = true)
    public MonthlyReportDto getMonthlyReport(Long userId, String month) {
        List<AttendanceDay> days = repository
                .findByUserIdAndDateStartingWithOrderByDateAsc(userId, month);

        int trained = 0, missed = 0, rest = 0, exercises = 0, minutes = 0;
        int longest = 0, running = 0, current = 0;

        for (AttendanceDay d : days) {
            switch (d.getStatus() == null ? TRAINED : d.getStatus()) {
                case TRAINED -> {
                    trained++;
                    running++;
                    longest = Math.max(longest, running);
                    exercises += d.getExercisesCompleted() == null ? 0 : d.getExercisesCompleted();
                    minutes += d.getDurationMinutes() == null ? 0 : d.getDurationMinutes();
                }
                case MISSED -> {
                    missed++;
                    running = 0;
                }
                case REST -> rest++;
                default -> { }
            }
        }
        // Racha actual: días entrenados consecutivos al final del periodo
        for (int i = days.size() - 1; i >= 0; i--) {
            String status = days.get(i).getStatus();
            if (TRAINED.equals(status)) {
                current++;
            } else if (MISSED.equals(status)) {
                break;
            }
        }

        int base = trained + missed;
        int adherence = base == 0 ? 0 : Math.round((trained * 100f) / base);

        return MonthlyReportDto.builder()
                .month(month)
                .daysTrained(trained)
                .daysMissed(missed)
                .daysRest(rest)
                .totalExercises(exercises)
                .totalMinutes(minutes)
                .adherencePercent(adherence)
                .longestStreak(longest)
                .currentStreak(current)
                .days(days.stream().map(AttendanceDayDto::fromEntity).toList())
                .build();
    }
}
