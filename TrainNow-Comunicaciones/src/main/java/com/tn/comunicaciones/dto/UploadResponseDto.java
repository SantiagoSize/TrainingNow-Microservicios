package com.tn.comunicaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta al subir un adjunto de chat (imagen/video). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponseDto {

    /** URL relativa del archivo guardado (ej. "/uploads/chat/uuid.jpg"). */
    private String url;

    /** "IMAGE" o "VIDEO", detectado por el content-type del archivo subido. */
    private String attachmentType;
}
