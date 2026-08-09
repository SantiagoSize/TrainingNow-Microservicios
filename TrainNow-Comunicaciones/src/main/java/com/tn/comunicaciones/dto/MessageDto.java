package com.tn.comunicaciones.dto;

import com.tn.comunicaciones.model.Mensaje;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** DTO de mensaje de chat. Contrato exacto con el cliente Android. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

    private Long id;

    @NotNull(message = "senderId es obligatorio")
    private Long senderId;

    @NotNull(message = "receiverId es obligatorio")
    private Long receiverId;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    private Long timestamp;
    private Boolean isRead;

    public static MessageDto fromEntity(Mensaje m) {
        return MessageDto.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .receiverId(m.getReceiverId())
                .content(m.getContent())
                .timestamp(m.getTimestamp())
                .isRead(m.getIsRead())
                .build();
    }

    public Mensaje toEntity() {
        return Mensaje.builder()
                .id(id != null && id > 0 ? id : null)
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .timestamp(timestamp)
                .isRead(isRead != null ? isRead : false)
                .build();
    }
}
