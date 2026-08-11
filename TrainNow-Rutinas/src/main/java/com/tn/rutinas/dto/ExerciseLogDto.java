package com.tn.rutinas.dto;

import com.tn.rutinas.model.ExerciseLog;
import lombok.*;

/** DTO de log de ejercicio. Contrato exacto con el cliente Android. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLogDto {

    private Long id;
    private Long sessionId;
    private Long exerciseId;
    private Integer orderInSession;
    private Integer plannedSets;
    private Integer plannedReps;
    private Double plannedWeightKg;
    private Integer completedSets;
    private String actualReps;
    private Double weightKg;
    private Integer restTimeSeconds;
    private Integer durationSeconds;
    private String notes;
    private Integer rpe;
    private Boolean isPersonalRecord;
    private Integer formRating;
    private String tempo;
    private Long createdAt;

    public static ExerciseLogDto fromEntity(ExerciseLog e) {
        return ExerciseLogDto.builder()
                .id(e.getId())
                .sessionId(e.getSessionId())
                .exerciseId(e.getExerciseId())
                .orderInSession(e.getOrderInSession())
                .plannedSets(e.getPlannedSets())
                .plannedReps(e.getPlannedReps())
                .plannedWeightKg(e.getPlannedWeightKg())
                .completedSets(e.getCompletedSets())
                .actualReps(e.getActualReps())
                .weightKg(e.getWeightKg())
                .restTimeSeconds(e.getRestTimeSeconds())
                .durationSeconds(e.getDurationSeconds())
                .notes(e.getNotes())
                .rpe(e.getRpe())
                .isPersonalRecord(e.getIsPersonalRecord())
                .formRating(e.getFormRating())
                .tempo(e.getTempo())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public ExerciseLog toEntity() {
        return ExerciseLog.builder()
                .id(id != null && id > 0 ? id : null)
                .sessionId(sessionId)
                .exerciseId(exerciseId)
                .orderInSession(orderInSession != null ? orderInSession : 0)
                .plannedSets(plannedSets != null ? plannedSets : 3)
                .plannedReps(plannedReps != null ? plannedReps : 12)
                .plannedWeightKg(plannedWeightKg)
                .completedSets(completedSets != null ? completedSets : 0)
                .actualReps(actualReps)
                .weightKg(weightKg)
                .restTimeSeconds(restTimeSeconds != null ? restTimeSeconds : 60)
                .durationSeconds(durationSeconds)
                .notes(notes)
                .rpe(rpe)
                .isPersonalRecord(isPersonalRecord != null ? isPersonalRecord : false)
                .formRating(formRating)
                .tempo(tempo)
                .build();
    }
}
