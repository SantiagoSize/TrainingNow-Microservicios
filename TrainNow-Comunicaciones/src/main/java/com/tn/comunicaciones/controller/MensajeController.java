package com.tn.comunicaciones.controller;

import com.tn.comunicaciones.dto.ConversationSummaryDto;
import com.tn.comunicaciones.dto.MessageDto;
import com.tn.comunicaciones.dto.UploadResponseDto;
import com.tn.comunicaciones.service.MensajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * API REST del chat. Contrato consumido por ChatApi.kt (Android).
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService service;

    @GetMapping("/conversation/{userA}/{userB}")
    public List<MessageDto> getConversation(@PathVariable Long userA, @PathVariable Long userB) {
        return service.getConversation(userA, userB);
    }

    @GetMapping("/user/{userId}")
    public List<MessageDto> getByParticipant(@PathVariable Long userId) {
        return service.getByParticipant(userId);
    }

    /** Lista de chats con último mensaje + no leídos por contacto (para la pantalla de conversaciones). */
    @GetMapping("/conversations/{userId}")
    public List<ConversationSummaryDto> getConversationsSummary(@PathVariable Long userId) {
        return service.getConversationsSummary(userId);
    }

    @PostMapping
    public ResponseEntity<MessageDto> create(@Valid @RequestBody MessageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{id}/read")
    public MessageDto markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    /** Sube una imagen o video del chat (ya comprimido por el cliente) y devuelve su URL. */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDto> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.storeFile(file));
    }
}
