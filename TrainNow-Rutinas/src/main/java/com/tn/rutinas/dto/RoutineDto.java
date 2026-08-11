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

    public static RoutineDto fromEntity(Rutina r) {
        return RoutineDto.builder()
                .id(r.getId())
                .ownerId(r.getOwnerId())
                .creatorId(r.getCreatorId())
                .name(r.getName())
                .dayInfo(r.getDayInfo())
                .creationDate(r.getCreationDate())
                .scheduledTime(r.getScheduledTime())
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
                .build();
    }
}
