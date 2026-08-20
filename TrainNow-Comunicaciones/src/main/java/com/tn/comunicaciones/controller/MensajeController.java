package com.tn.comunicaciones.controller;

import com.tn.comunicaciones.dto.ConversationSummaryDto;
import com.tn.comunicaciones.dto.MessageDto;
import com.tn.comunicaciones.dto.UploadResponseDto;
import com.tn.comunicaciones.service.MensajeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponse(responseCode = "200", description = "Mensajes entre los dos usuarios")
    public List<MessageDto> getConversation(@PathVariable Long userA, @PathVariable Long userB) {
        return service.getConversation(userA, userB);
    }

    @GetMapping("/user/{userId}")
    @ApiResponse(responseCode = "200", description = "Mensajes donde el usuario participa")
    public List<MessageDto> getByParticipant(@PathVariable Long userId) {
        return service.getByParticipant(userId);
    }

    /** Lista de chats con último mensaje + no leídos por contacto (para la pantalla de conversaciones). */
    @GetMapping("/conversations/{userId}")
    @ApiResponse(responseCode = "200", description = "Resumen de conversaciones (último mensaje + no leídos)")
    public List<ConversationSummaryDto> getConversationsSummary(@PathVariable Long userId) {
        return service.getConversationsSummary(userId);
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mensaje enviado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<MessageDto> create(@Valid @RequestBody MessageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{id}/read")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensaje marcado como leído"),
            @ApiResponse(responseCode = "404", description = "El mensaje no existe")
    })
    public MessageDto markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    /** Sube una imagen o video del chat (ya comprimido por el cliente) y devuelve su URL. */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Adjunto subido, devuelve URL relativa y tipo"),
            @ApiResponse(responseCode = "400", description = "Archivo vacío o formato no soportado")
    })
    public ResponseEntity<UploadResponseDto> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.storeFile(file));
    }
}
