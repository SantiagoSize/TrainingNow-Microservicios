package com.tn.biblioteca.service;

import com.tn.biblioteca.dto.EjercicioDTO;
import com.tn.biblioteca.dto.EjercicioRequestDTO;
import com.tn.biblioteca.converter.EjercicioConverter;
import com.tn.biblioteca.model.Ejercicio;
import com.tn.biblioteca.model.Dificultad;
import com.tn.biblioteca.model.GrupoMuscular;
import com.tn.biblioteca.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null") // Evita falsos positivos del analizador de null-safety con APIs de Spring Data
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final EjercicioConverter ejercicioConverter;

    @Transactional(readOnly = true)
    public List<EjercicioDTO> findAll() {
        return ejercicioRepository.findAll().stream()
                .map(ejercicioConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EjercicioDTO findById(Long id) {
        return ejercicioRepository.findById(id)
                .map(ejercicioConverter::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<EjercicioDTO> findByGrupoMuscular(GrupoMuscular grupoMuscular) {
        return ejercicioRepository.findByGrupoMuscular(grupoMuscular).stream()
                .map(ejercicioConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EjercicioDTO> findByDificultad(Dificultad dificultad) {
        return ejercicioRepository.findByDificultad(dificultad).stream()
                .map(ejercicioConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EjercicioDTO create(EjercicioRequestDTO request) {
        Ejercicio entity = ejercicioConverter.toEntity(request);
        entity = ejercicioRepository.save(entity);
        return ejercicioConverter.toDTO(entity);
    }

    @Transactional
    public EjercicioDTO update(Long id, EjercicioRequestDTO request) {
        Ejercicio entity = ejercicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado: " + id));
        ejercicioConverter.updateEntity(entity, request);
        entity = ejercicioRepository.save(entity);
        return ejercicioConverter.toDTO(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!ejercicioRepository.existsById(id)) {
            throw new IllegalArgumentException("Ejercicio no encontrado: " + id);
        }
        ejercicioRepository.deleteById(id);
    }
}
