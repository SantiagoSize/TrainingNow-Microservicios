package com.tn.comunicaciones.controller;

import com.tn.comunicaciones.dto.MessageDto;
import com.tn.comunicaciones.service.MensajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<MessageDto> create(@Valid @RequestBody MessageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{id}/read")
    public MessageDto markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }
}
