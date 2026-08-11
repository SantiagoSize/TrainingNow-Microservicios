package com.tn.rutinas.dto;

import com.tn.rutinas.model.EjercicioRutina;
import lombok.*;

/** DTO de ejercicio de rutina. Contrato exacto con el cliente Android (campo "order"). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineExerciseDto {

    private Long routineId;
    private Long exerciseId;
    private Integer order;

    public static RoutineExerciseDto fromEntity(EjercicioRutina e) {
        return RoutineExerciseDto.builder()
                .routineId(e.getRoutineId())
                .exerciseId(e.getExerciseId())
                .order(e.getOrderIndex())
                .build();
    }

    public EjercicioRutina toEntity() {
        return EjercicioRutina.builder()
                .routineId(routineId)
                .exerciseId(exerciseId)
                .orderIndex(order != null ? order : 0)
                .build();
    }
}
