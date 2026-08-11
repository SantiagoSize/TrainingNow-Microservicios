package com.tn.comunicaciones.service;

import com.tn.comunicaciones.dto.MessageDto;
import com.tn.comunicaciones.exception.ResourceNotFoundException;
import com.tn.comunicaciones.model.Mensaje;
import com.tn.comunicaciones.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio del chat. */
@Service
@RequiredArgsConstructor
@Transactional
public class MensajeService {

    private final MensajeRepository repository;

    @Transactional(readOnly = true)
    public List<MessageDto> getConversation(Long a, Long b) {
        return repository.findConversation(a, b).stream().map(MessageDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getByParticipant(Long userId) {
        return repository.findByParticipant(userId).stream().map(MessageDto::fromEntity).toList();
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
}
