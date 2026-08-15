package com.tn.usuarios.dto;

import com.tn.usuarios.model.AuditLog;
import lombok.*;

/**
 * DTO de registro de actividad. Contrato con el cliente Android.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDto {

    private Long id;
    private Long actorId;
    private String actorName;
    private String actorRole;
    private String action;
    private String targetType;
    private Long targetId;
    private String targetName;
    private String details;
    private Long timestamp;

    public static AuditLogDto fromEntity(AuditLog log) {
        return AuditLogDto.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorName(log.getActorName())
                .actorRole(log.getActorRole())
                .action(log.getAction())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .targetName(log.getTargetName())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }

    public AuditLog toEntity() {
        return AuditLog.builder()
                .actorId(actorId)
                .actorName(actorName)
                .actorRole(actorRole)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .targetName(targetName)
                .details(details)
                .timestamp(timestamp)
                .build();
    }
}
