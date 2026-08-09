package com.tn.usuarios.dto;

import com.tn.usuarios.model.TrainerClient;
import lombok.*;

/**
 * DTO de relación entrenador-cliente. Contrato exacto con el cliente Android.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerClientDto {

    private Long trainerId;
    private Long clientId;
    private String status;
    private Long startDate;
    private Long endDate;
    private String trainerNotes;
    private String clientGoals;
    private Double sessionPrice;
    private Integer sessionsPerWeek;
    private Long lastInteractionDate;
    private Long createdAt;
    private Long updatedAt;

    public static TrainerClientDto fromEntity(TrainerClient tc) {
        return TrainerClientDto.builder()
                .trainerId(tc.getTrainerId())
                .clientId(tc.getClientId())
                .status(tc.getStatus())
                .startDate(tc.getStartDate())
                .endDate(tc.getEndDate())
                .trainerNotes(tc.getTrainerNotes())
                .clientGoals(tc.getClientGoals())
                .sessionPrice(tc.getSessionPrice())
                .sessionsPerWeek(tc.getSessionsPerWeek())
                .lastInteractionDate(tc.getLastInteractionDate())
                .createdAt(tc.getCreatedAt())
                .updatedAt(tc.getUpdatedAt())
                .build();
    }

    public TrainerClient toEntity() {
        return TrainerClient.builder()
                .trainerId(trainerId)
                .clientId(clientId)
                .status(status != null ? status : "PENDING")
                .startDate(startDate)
                .endDate(endDate)
                .trainerNotes(trainerNotes)
                .clientGoals(clientGoals)
                .sessionPrice(sessionPrice)
                .sessionsPerWeek(sessionsPerWeek != null ? sessionsPerWeek : 3)
                .lastInteractionDate(lastInteractionDate)
                .build();
    }
}
