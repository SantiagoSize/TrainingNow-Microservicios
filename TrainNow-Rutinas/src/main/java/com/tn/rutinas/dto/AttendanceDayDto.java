package com.tn.rutinas.dto;

import com.tn.rutinas.model.AttendanceDay;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** DTO de asistencia diaria. Contrato con el cliente Android. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDayDto {

    private Long id;

    @NotNull(message = "userId es obligatorio")
    private Long userId;

    @NotNull(message = "date es obligatoria (yyyy-MM-dd)")
    private String date;

    private String status;
    private Long routineId;
    private Integer exercisesCompleted;
    private Integer durationMinutes;
    private Long createdAt;

    public static AttendanceDayDto fromEntity(AttendanceDay a) {
        return AttendanceDayDto.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .date(a.getDate())
                .status(a.getStatus())
                .routineId(a.getRoutineId())
                .exercisesCompleted(a.getExercisesCompleted())
                .durationMinutes(a.getDurationMinutes())
                .createdAt(a.getCreatedAt())
                .build();
    }

    public AttendanceDay toEntity() {
        return AttendanceDay.builder()
                .userId(userId)
                .date(date)
                .status(status != null ? status : "TRAINED")
                .routineId(routineId)
                .exercisesCompleted(exercisesCompleted != null ? exercisesCompleted : 0)
                .durationMinutes(durationMinutes)
                .build();
    }
}
