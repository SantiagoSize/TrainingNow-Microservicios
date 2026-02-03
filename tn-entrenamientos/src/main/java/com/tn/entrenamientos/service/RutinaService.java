package com.tn.entrenamientos.service;

import com.tn.entrenamientos.converter.RutinaConverter;
import com.tn.entrenamientos.dto.RutinaDTO;
import com.tn.entrenamientos.dto.RutinaEjercicioDTO;
import com.tn.entrenamientos.dto.RutinaEjercicioRequestDTO;
import com.tn.entrenamientos.dto.RutinaRequestDTO;
import com.tn.entrenamientos.model.Rutina;
import com.tn.entrenamientos.model.RutinaEjercicio;
import com.tn.entrenamientos.repository.RutinaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final RutinaConverter rutinaConverter;

    @Transactional
    public RutinaDTO crearRutina(RutinaRequestDTO request) {
        Rutina rutina = rutinaConverter.toEntity(request);
        Rutina guardada = rutinaRepository.save(
                Objects.requireNonNull(rutina, "rutina must not be null")
        );
        return rutinaConverter.toDto(guardada);
    }

    @Transactional(readOnly = true)
    public List<RutinaDTO> obtenerRutinasPorOwner(Long ownerId) {
        return rutinaRepository
                .findByOwnerId(Objects.requireNonNull(ownerId, "ownerId must not be null"))
                .stream()
                .map(rutinaConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RutinaDTO> obtenerRutinasPorCreator(Long creatorId) {
        return rutinaRepository
                .findByCreatorId(Objects.requireNonNull(creatorId, "creatorId must not be null"))
                .stream()
                .map(rutinaConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<RutinaEjercicioDTO> asignarEjercicios(Long rutinaId, List<RutinaEjercicioRequestDTO> ejerciciosRequest) {
        Rutina rutina = rutinaRepository
                .findById(Objects.requireNonNull(rutinaId, "rutinaId must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + rutinaId));

        List<RutinaEjercicio> nuevosEjercicios = new ArrayList<>();
        if (ejerciciosRequest != null) {
            for (RutinaEjercicioRequestDTO req : ejerciciosRequest) {
                RutinaEjercicio ejercicio = RutinaEjercicio.builder()
                        .rutina(rutina)
                        .ejercicioId(req.getEjercicioId())
                        .series(req.getSeries())
                        .repeticiones(req.getRepeticiones())
                        .descanso(req.getDescanso())
                        .observaciones(req.getObservaciones())
                        .build();
                nuevosEjercicios.add(ejercicio);
            }
        }

        rutina.getEjercicios().clear();
        rutina.getEjercicios().addAll(nuevosEjercicios);

        Rutina guardada = rutinaRepository.save(
                Objects.requireNonNull(rutina, "rutina must not be null")
        );
        return rutinaConverter.toEjercicioDtoList(guardada.getEjercicios());
    }

    @Transactional(readOnly = true)
    public List<RutinaEjercicioDTO> obtenerEjerciciosPorRutina(Long rutinaId) {
        Rutina rutina = rutinaRepository
                .findById(Objects.requireNonNull(rutinaId, "rutinaId must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + rutinaId));
        return rutinaConverter.toEjercicioDtoList(rutina.getEjercicios());
    }
}
