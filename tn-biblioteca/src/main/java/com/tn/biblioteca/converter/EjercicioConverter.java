package com.tn.biblioteca.converter;

import com.tn.biblioteca.dto.EjercicioDTO;
import com.tn.biblioteca.dto.EjercicioRequestDTO;
import com.tn.biblioteca.model.Ejercicio;
import org.springframework.stereotype.Component;

@Component
public class EjercicioConverter {

    public EjercicioDTO toDTO(Ejercicio entity) {
        if (entity == null) return null;
        return new EjercicioDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getGrupoMuscular(),
                entity.getDificultad(),
                entity.getEquipamiento(),
                entity.getUrlVideo(),
                entity.getCaloriasEstimadas()
        );
    }

    public Ejercicio toEntity(EjercicioRequestDTO request) {
        if (request == null) return null;
        return Ejercicio.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .grupoMuscular(request.getGrupoMuscular())
                .dificultad(request.getDificultad())
                .equipamiento(request.getEquipamiento())
                .urlVideo(request.getUrlVideo())
                .caloriasEstimadas(request.getCaloriasEstimadas())
                .build();
    }

    public void updateEntity(Ejercicio entity, EjercicioRequestDTO request) {
        if (entity == null || request == null) return;
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setGrupoMuscular(request.getGrupoMuscular());
        entity.setDificultad(request.getDificultad());
        entity.setEquipamiento(request.getEquipamiento());
        entity.setUrlVideo(request.getUrlVideo());
        entity.setCaloriasEstimadas(request.getCaloriasEstimadas());
    }
}
