package com.tn.entrenamientos.service;

import com.tn.entrenamientos.dto.SesionEntrenamientoDTO;
import com.tn.entrenamientos.dto.SesionEntrenamientoRequestDTO;
import com.tn.entrenamientos.model.RutinaEjercicio;
import com.tn.entrenamientos.model.SesionEntrenamiento;
import com.tn.entrenamientos.repository.RutinaEjercicioRepository;
import com.tn.entrenamientos.repository.SesionEntrenamientoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SesionEntrenamientoService {

    private final SesionEntrenamientoRepository sesionRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;

    @Transactional
    @SuppressWarnings("null")
    public SesionEntrenamientoDTO registrarSesion(SesionEntrenamientoRequestDTO request) {
        RutinaEjercicio rutinaEjercicio = rutinaEjercicioRepository
                .findById(Objects.requireNonNull(request.getRutinaEjercicioId(), "rutinaEjercicioId must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("RutinaEjercicio no encontrada: " + request.getRutinaEjercicioId()));

        SesionEntrenamiento sesion = SesionEntrenamiento.builder()
                .userId(request.getUserId())
                .rutinaEjercicio(rutinaEjercicio)
                .fechaHora(LocalDateTime.now())
                .seriesRealizadas(request.getSeriesRealizadas())
                .repeticionesPorSerie(request.getRepeticionesPorSerie())
                .pesoLevantado(request.getPesoLevantado())
                .build();

        SesionEntrenamiento guardada = Objects.requireNonNull(
                sesionRepository.save(sesion),
                "saved session must not be null"
        );
        return toDto(guardada);
    }

    @Transactional(readOnly = true)
    public List<SesionEntrenamientoDTO> obtenerSesionesPorUsuario(Long userId) {
        return sesionRepository
                .findByUserIdOrderByFechaHoraDesc(Objects.requireNonNull(userId, "userId must not be null"))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private SesionEntrenamientoDTO toDto(SesionEntrenamiento sesion) {
        SesionEntrenamientoDTO dto = new SesionEntrenamientoDTO();
        dto.setId(sesion.getId());
        dto.setUserId(sesion.getUserId());
        dto.setRutinaEjercicioId(sesion.getRutinaEjercicio().getId());
        dto.setFechaHora(sesion.getFechaHora());
        dto.setSeriesRealizadas(sesion.getSeriesRealizadas());
        dto.setRepeticionesPorSerie(sesion.getRepeticionesPorSerie());
        dto.setPesoLevantado(sesion.getPesoLevantado());
        return dto;
    }
}
