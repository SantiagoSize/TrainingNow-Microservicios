package com.tn.rutinas.config;

import com.tn.rutinas.model.EjercicioRutina;
import com.tn.rutinas.model.Rutina;
import com.tn.rutinas.repository.EjercicioRutinaRepository;
import com.tn.rutinas.repository.RutinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Seed inicial: una rutina pública de ejemplo (solo si la tabla está vacía). */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RutinaRepository rutinaRepository;
    private final EjercicioRutinaRepository ejercicioRutinaRepository;

    @Override
    public void run(String... args) {
        if (rutinaRepository.count() > 0) return;

        Rutina fullBody = rutinaRepository.save(Rutina.builder()
                .ownerId(null) // pública
                .creatorId(2L) // entrenador seed de TrainNow-Usuarios
                .name("Full Body Principiante")
                .dayInfo("Lunes, Miércoles, Viernes")
                .build());

        ejercicioRutinaRepository.saveAll(List.of(
                EjercicioRutina.builder().routineId(fullBody.getId()).exerciseId(7L).orderIndex(0).build(),  // Sentadilla
                EjercicioRutina.builder().routineId(fullBody.getId()).exerciseId(1L).orderIndex(1).build(),  // Press banca
                EjercicioRutina.builder().routineId(fullBody.getId()).exerciseId(5L).orderIndex(2).build(),  // Remo con barra
                EjercicioRutina.builder().routineId(fullBody.getId()).exerciseId(10L).orderIndex(3).build(), // Press militar
                EjercicioRutina.builder().routineId(fullBody.getId()).exerciseId(14L).orderIndex(4).build()  // Plancha
        ));
    }
}
