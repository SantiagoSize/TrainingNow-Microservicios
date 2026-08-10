package com.tn.rutinas.dto;

import lombok.*;

import java.util.List;

/** Reporte mensual de entrenamiento de un usuario. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyReportDto {

    /** Formato yyyy-MM. */
    private String month;

    private Integer daysTrained;
    private Integer daysMissed;
    private Integer daysRest;
    private Integer totalExercises;
    private Integer totalMinutes;

    /** Porcentaje de adherencia: entrenados / (entrenados + perdidos) * 100. */
    private Integer adherencePercent;

    /** Racha más larga de días entrenados consecutivos. */
    private Integer longestStreak;

    /** Racha actual de días entrenados. */
    private Integer currentStreak;

    /** Detalle día a día del mes. */
    private List<AttendanceDayDto> days;
}
