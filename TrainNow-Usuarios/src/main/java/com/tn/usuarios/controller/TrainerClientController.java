package com.tn.usuarios.controller;

import com.tn.usuarios.dto.TrainerClientDto;
import com.tn.usuarios.service.TrainerClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de relaciones entrenador-cliente. Contrato consumido por UserApi.kt (Android).
 */
@RestController
@RequestMapping("/api/trainer-clients")
@RequiredArgsConstructor
public class TrainerClientController {

    private final TrainerClientService service;

    @GetMapping("/trainer/{trainerId}")
    public List<TrainerClientDto> getByTrainer(@PathVariable Long trainerId) {
        return service.getByTrainer(trainerId);
    }

    @GetMapping("/trainer/{trainerId}/status/{status}")
    public List<TrainerClientDto> getByTrainerAndStatus(@PathVariable Long trainerId,
                                                        @PathVariable String status) {
        return service.getByTrainerAndStatus(trainerId, status);
    }

    @GetMapping("/client/{clientId}")
    public List<TrainerClientDto> getByClient(@PathVariable Long clientId) {
        return service.getByClient(clientId);
    }

    @PostMapping
    public ResponseEntity<TrainerClientDto> create(@Valid @RequestBody TrainerClientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
}
