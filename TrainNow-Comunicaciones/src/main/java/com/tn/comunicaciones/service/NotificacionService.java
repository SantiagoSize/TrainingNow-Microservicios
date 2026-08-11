package com.tn.comunicaciones.service;

import com.tn.comunicaciones.dto.NotificationDto;
import com.tn.comunicaciones.exception.ResourceNotFoundException;
import com.tn.comunicaciones.model.Notificacion;
import com.tn.comunicaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio de notificaciones. */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificacionService {

    private final NotificacionRepository repository;

    @Transactional(readOnly = true)
    public NotificationDto getById(Long id) {
        return NotificationDto.fromEntity(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getByUser(Long userId) {
        return repository.findByUserIdOrderByDateDesc(userId)
                .stream().map(NotificationDto::fromEntity).toList();
    }

    public NotificationDto create(NotificationDto dto) {
        Notificacion n = dto.toEntity();
        n.setId(null);
        return NotificationDto.fromEntity(repository.save(n));
    }

    public NotificationDto update(Long id, NotificationDto dto) {
        Notificacion existing = findOrThrow(id);
        existing.setTitle(dto.getTitle());
        existing.setMessage(dto.getMessage());
        existing.setType(dto.getType() != null ? dto.getType() : existing.getType());
        existing.setDate(dto.getDate() != null ? dto.getDate() : existing.getDate());
        existing.setIsRead(dto.getIsRead() != null ? dto.getIsRead() : existing.getIsRead());
        existing.setActionType(dto.getActionType());
        existing.setActionData(dto.getActionData());
        existing.setPriority(dto.getPriority() != null ? dto.getPriority() : existing.getPriority());
        existing.setExpiresAt(dto.getExpiresAt());
        existing.setSenderId(dto.getSenderId());
        existing.setIconUrl(dto.getIconUrl());
        return NotificationDto.fromEntity(repository.save(existing));
    }

    public NotificationDto markAsRead(Long id) {
        Notificacion n = findOrThrow(id);
        n.setIsRead(true);
        return NotificationDto.fromEntity(repository.save(n));
    }

    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Notificacion findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada: id=" + id));
    }
}
