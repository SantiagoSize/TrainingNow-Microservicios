package com.tn.comunicaciones.dto;

import com.tn.comunicaciones.model.Notificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** DTO de notificación. Contrato exacto con el cliente Android. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

    private Long id;
    @NotNull(message = "El userId es obligatorio")
    private Long userId;
    @NotBlank(message = "El título es obligatorio")
    private String title;
    @NotBlank(message = "El mensaje es obligatorio")
    private String message;
    private String type;
    private Long date;
    private Boolean isRead;
    private String actionType;
    private String actionData;
    private String priority;
    private Long expiresAt;
    private Long senderId;
    private String iconUrl;
    private Long createdAt;

    public static NotificationDto fromEntity(Notificacion n) {
        return NotificationDto.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .date(n.getDate())
                .isRead(n.getIsRead())
                .actionType(n.getActionType())
                .actionData(n.getActionData())
                .priority(n.getPriority())
                .expiresAt(n.getExpiresAt())
                .senderId(n.getSenderId())
                .iconUrl(n.getIconUrl())
                .createdAt(n.getCreatedAt())
                .build();
    }

    public Notificacion toEntity() {
        return Notificacion.builder()
                .id(id != null && id > 0 ? id : null)
                .userId(userId)
                .title(title)
                .message(message)
                .type(type != null ? type : "SYSTEM")
                .date(date)
                .isRead(isRead != null ? isRead : false)
                .actionType(actionType)
                .actionData(actionData)
                .priority(priority != null ? priority : "NORMAL")
                .expiresAt(expiresAt)
                .senderId(senderId)
                .iconUrl(iconUrl)
                .build();
    }
}
