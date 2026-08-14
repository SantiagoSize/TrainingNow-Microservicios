package com.tn.comunicaciones.service;

import com.tn.comunicaciones.dto.ConversationSummaryDto;
import com.tn.comunicaciones.dto.MessageDto;
import com.tn.comunicaciones.dto.UploadResponseDto;
import com.tn.comunicaciones.exception.ResourceNotFoundException;
import com.tn.comunicaciones.model.Mensaje;
import com.tn.comunicaciones.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Lógica de negocio del chat. */
@Service
@RequiredArgsConstructor
@Transactional
public class MensajeService {

    private final MensajeRepository repository;

    @Value("${app.upload.dir:uploads/chat}")
    private String uploadDir;

    /** Tamaño máximo aceptado por adjunto (imagen ya comprimida o video), en bytes. */
    private static final long MAX_ATTACHMENT_BYTES = 20L * 1024 * 1024; // 20 MB

    @Transactional(readOnly = true)
    public List<MessageDto> getConversation(Long a, Long b) {
        return repository.findConversation(a, b).stream().map(MessageDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getByParticipant(Long userId) {
        return repository.findByParticipant(userId).stream().map(MessageDto::fromEntity).toList();
    }

    /**
     * Resumen de conversaciones para la lista de chats: agrupa los mensajes del usuario por
     * interlocutor y se queda con el más reciente de cada uno (findByParticipant ya viene
     * ordenado DESC por timestamp, así que el primer mensaje visto por contacto es el último).
     */
    @Transactional(readOnly = true)
    public List<ConversationSummaryDto> getConversationsSummary(Long userId) {
        List<Mensaje> mensajes = repository.findByParticipant(userId);
        Map<Long, ConversationSummaryDto> resumen = new LinkedHashMap<>();

        for (Mensaje m : mensajes) {
            Long otroId = m.getSenderId().equals(userId) ? m.getReceiverId() : m.getSenderId();
            ConversationSummaryDto actual = resumen.get(otroId);
            if (actual == null) {
                actual = ConversationSummaryDto.builder()
                        .contactId(otroId)
                        .lastMessage(m.getContent())
                        .lastAttachmentType(m.getAttachmentType())
                        .lastTimestamp(m.getTimestamp())
                        .unreadCount(0L)
                        .build();
                resumen.put(otroId, actual);
            }
            if (m.getReceiverId().equals(userId) && Boolean.FALSE.equals(m.getIsRead())) {
                actual.setUnreadCount(actual.getUnreadCount() + 1);
            }
        }
        return resumen.values().stream().toList();
    }

    public MessageDto create(MessageDto dto) {
        Mensaje m = dto.toEntity();
        m.setId(null);
        return MessageDto.fromEntity(repository.save(m));
    }

    public MessageDto markAsRead(Long id) {
        Mensaje m = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje no encontrado: id=" + id));
        m.setIsRead(true);
        return MessageDto.fromEntity(repository.save(m));
    }

    /**
     * Guarda un adjunto de chat (imagen o video) en disco y devuelve su URL relativa.
     * La compresión real (calidad de imagen, duración/calidad de video) ocurre en el
     * cliente antes de subir; acá solo se valida tipo/tamaño y se persiste el archivo.
     */
    public UploadResponseDto storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        if (file.getSize() > MAX_ATTACHMENT_BYTES) {
            throw new IllegalArgumentException("El archivo supera el máximo permitido (20 MB)");
        }

        String contentType = file.getContentType() != null ? file.getContentType() : "";
        String tipo;
        if (contentType.startsWith("image/")) {
            tipo = "IMAGE";
        } else if (contentType.startsWith("video/")) {
            tipo = "VIDEO";
        } else {
            throw new IllegalArgumentException("Tipo de archivo no soportado: " + contentType);
        }

        String extension = "";
        String nombreOriginal = file.getOriginalFilename();
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
        }
        String nombreArchivo = UUID.randomUUID() + extension;

        Path carpeta = new File(uploadDir).toPath();
        Files.createDirectories(carpeta);
        Path destino = carpeta.resolve(nombreArchivo);
        file.transferTo(destino);

        return UploadResponseDto.builder()
                .url("/uploads/chat/" + nombreArchivo)
                .attachmentType(tipo)
                .build();
    }
}
