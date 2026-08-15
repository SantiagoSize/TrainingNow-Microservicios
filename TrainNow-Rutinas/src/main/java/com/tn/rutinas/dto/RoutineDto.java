package com.tn.rutinas.dto;

import com.tn.rutinas.model.Rutina;
import lombok.*;

/** DTO de rutina. Contrato exacto con el cliente Android. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineDto {

    private Long id;
    private Long ownerId;
    private Long creatorId;
    private String name;
    private String dayInfo;
    private Long creationDate;
    private Long scheduledTime;
    /** true = compartida por un entrenador, pendiente de que el usuario la acepte. */
    private Boolean pendingShare;
    /** true = plantilla reutilizable del entrenador (no es rutina global de admin). */
    private Boolean isTemplate;

    public static RoutineDto fromEntity(Rutina r) {
        return RoutineDto.builder()
                .id(r.getId())
                .ownerId(r.getOwnerId())
                .creatorId(r.getCreatorId())
                .name(r.getName())
                .dayInfo(r.getDayInfo())
                .creationDate(r.getCreationDate())
                .scheduledTime(r.getScheduledTime())
                .pendingShare(r.getPendingShare())
                .isTemplate(r.getIsTemplate())
                .build();
    }

    public Rutina toEntity() {
        return Rutina.builder()
                .id(id != null && id > 0 ? id : null)
                .ownerId(ownerId)
                .creatorId(creatorId != null ? creatorId : 0L)
                .name(name)
                .dayInfo(dayInfo)
                .creationDate(creationDate)
                .scheduledTime(scheduledTime)
                .pendingShare(pendingShare != null ? pendingShare : false)
                .isTemplate(isTemplate != null ? isTemplate : false)
                .build();
    }
}
