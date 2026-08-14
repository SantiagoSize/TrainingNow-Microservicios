package com.tn.comunicaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resumen de una conversación para la lista de chats (último mensaje + no leídos),
 * en vez de traer todos los mensajes sueltos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationSummaryDto {

    /** ID del otro participante de la conversación. */
    private Long contactId;

    private String lastMessage;

    /** "IMAGE" o "VIDEO" si el último mensaje fue un adjunto, null si fue texto. */
    private String lastAttachmentType;

    private Long lastTimestamp;

    /** Mensajes no leídos que el usuario consultado tiene pendientes de este contacto. */
    private Long unreadCount;
}
