package com.tn.entrenamientos.converter;

import com.tn.entrenamientos.dto.RutinaDTO;
import com.tn.entrenamientos.dto.RutinaEjercicioDTO;
import com.tn.entrenamientos.dto.RutinaEjercicioRequestDTO;
import com.tn.entrenamientos.dto.RutinaRequestDTO;
import com.tn.entrenamientos.model.Rutina;
import com.tn.entrenamientos.model.RutinaEjercicio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RutinaConverter {

    public Rutina toEntity(RutinaRequestDTO request) {
        Rutina rutina = Rutina.builder()
                .ownerId(request.getOwnerId())
                .creatorId(request.getCreatorId())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaCreacion(LocalDateTime.now())
                .build();

        List<RutinaEjercicio> ejercicios = new ArrayList<>();
        if (request.getEjercicios() != null) {
            for (RutinaEjercicioRequestDTO ejercicioRequest : request.getEjercicios()) {
                RutinaEjercicio ejercicio = RutinaEjercicio.builder()
                        .rutina(rutina)
                        .ejercicioId(ejercicioRequest.getEjercicioId())
                        .series(ejercicioRequest.getSeries())
                        .repeticiones(ejercicioRequest.getRepeticiones())
                        .descanso(ejercicioRequest.getDescanso())
                        .observaciones(ejercicioRequest.getObservaciones())
                        .build();
                ejercicios.add(ejercicio);
            }
        }
        rutina.setEjercicios(ejercicios);
        return rutina;
    }

    public RutinaDTO toDto(Rutina rutina) {
        RutinaDTO dto = new RutinaDTO();
        dto.setId(rutina.getId());
        dto.setOwnerId(rutina.getOwnerId());
        dto.setCreatorId(rutina.getCreatorId());
        dto.setNombre(rutina.getNombre());
        dto.setDescripcion(rutina.getDescripcion());
        dto.setFechaCreacion(rutina.getFechaCreacion());
        dto.setEjercicios(toEjercicioDtoList(rutina.getEjercicios()));
        return dto;
    }

    public List<RutinaEjercicioDTO> toEjercicioDtoList(List<RutinaEjercicio> ejerciciosEntity) {
        List<RutinaEjercicioDTO> ejercicios = new ArrayList<>();
        if (ejerciciosEntity != null) {
            for (RutinaEjercicio ejercicio : ejerciciosEntity) {
                RutinaEjercicioDTO ejercicioDTO = new RutinaEjercicioDTO();
                ejercicioDTO.setEjercicioId(ejercicio.getEjercicioId());
                ejercicioDTO.setSeries(ejercicio.getSeries());
                ejercicioDTO.setRepeticiones(ejercicio.getRepeticiones());
                ejercicioDTO.setDescanso(ejercicio.getDescanso());
                ejercicioDTO.setObservaciones(ejercicio.getObservaciones());
                ejercicios.add(ejercicioDTO);
            }
        }
        return ejercicios;
    }
}
