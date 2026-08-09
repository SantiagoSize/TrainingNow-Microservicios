package com.tn.rutinas.dto;

import com.tn.rutinas.model.WorkoutSession;
import lombok.*;

/** DTO de sesión de entrenamiento. Contrato exacto con el cliente Android. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSessionDto {

    private Long id;
    private Long userId;
    private Long routineId;
    private Long startTime;
    private Long endTime;
    private String status;
    private Integer totalDurationMinutes;
    private Integer caloriesBurned;
    private String notes;
    private Integer rating;
    private Integer perceivedDifficulty;
    private String mood;
    private String location;
    private Long createdAt;

    public static WorkoutSessionDto fromEntity(WorkoutSession w) {
        return WorkoutSessionDto.builder()
                .id(w.getId())
                .userId(w.getUserId())
                .routineId(w.getRoutineId())
                .startTime(w.getStartTime())
                .endTime(w.getEndTime())
                .status(w.getStatus())
                .totalDurationMinutes(w.getTotalDurationMinutes())
                .caloriesBurned(w.getCaloriesBurned())
                .notes(w.getNotes())
                .rating(w.getRating())
                .perceivedDifficulty(w.getPerceivedDifficulty())
                .mood(w.getMood())
                .location(w.getLocation())
                .createdAt(w.getCreatedAt())
                .build();
    }

    public WorkoutSession toEntity() {
        return WorkoutSession.builder()
                .id(id != null && id > 0 ? id : null)
                .userId(userId)
                .routineId(routineId)
                .startTime(startTime)
                .endTime(endTime)
                .status(status != null ? status : "IN_PROGRESS")
                .totalDurationMinutes(totalDurationMinutes)
                .caloriesBurned(caloriesBurned)
                .notes(notes)
                .rating(rating)
                .perceivedDifficulty(perceivedDifficulty)
                .mood(mood)
                .location(location)
                .build();
    }
}
