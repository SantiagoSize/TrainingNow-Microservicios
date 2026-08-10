package com.tn.rutinas.config;

import com.tn.rutinas.model.EjercicioRutina;
import com.tn.rutinas.model.Rutina;
import com.tn.rutinas.repository.EjercicioRutinaRepository;
import com.tn.rutinas.repository.RutinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seed de rutinas públicas: Básquetbol, Fútbol, Hipertrofia y Pilates.
 *
 * Cada día de la semana se guarda como un registro de rutina con el mismo
 * nombre y su dayInfo ("Lunes - Pecho y Tríceps"); la app los agrupa por
 * nombre para mostrar una sola rutina con sus días.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final String[] SEMANA =
            {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    private final RutinaRepository rutinaRepository;
    private final EjercicioRutinaRepository ejercicioRutinaRepository;

    @Override
    public void run(String... args) {
        if (rutinaRepository.count() > 0) return;

        // ==================== BÁSQUETBOL ====================
        crearDia("Pretemporada de Básquetbol", 1L, 0, "Manejo de balón y tiro", List.of(29L, 30L, 33L, 36L));
        crearDia("Pretemporada de Básquetbol", 1L, 1, "Fuerza de tren inferior", List.of(9L, 10L, 8L, 13L, 22L));
        crearDia("Pretemporada de Básquetbol", 1L, 2, "", List.of());
        crearDia("Pretemporada de Básquetbol", 1L, 3, "Salto y potencia", List.of(31L, 26L, 13L, 35L));
        crearDia("Pretemporada de Básquetbol", 1L, 4, "Defensa y agilidad", List.of(32L, 27L, 29L, 34L));
        crearDia("Pretemporada de Básquetbol", 1L, 5, "Partido y tiro libre", List.of(34L, 30L, 36L, 28L));
        crearDia("Pretemporada de Básquetbol", 1L, 6, "", List.of());

        // ==================== FÚTBOL ====================
        crearDia("Preparación de Fútbol", 1L, 0, "Técnica y pase", List.of(46L, 47L, 50L, 53L));
        crearDia("Preparación de Fútbol", 1L, 1, "Fuerza de piernas", List.of(9L, 13L, 12L, 8L, 22L));
        crearDia("Preparación de Fútbol", 1L, 2, "Velocidad y agilidad", List.of(49L, 50L, 46L, 27L));
        crearDia("Preparación de Fútbol", 1L, 3, "", List.of());
        crearDia("Preparación de Fútbol", 1L, 4, "Definición y remate", List.of(48L, 51L, 47L, 53L));
        crearDia("Preparación de Fútbol", 1L, 5, "Partido", List.of(53L, 48L, 49L));
        crearDia("Preparación de Fútbol", 1L, 6, "Recuperación activa", List.of(52L, 44L));

        // ==================== HIPERTROFIA ====================
        crearDia("Hipertrofia - 5 días", 2L, 0, "Pecho y Tríceps", List.of(1L, 2L, 3L, 20L, 21L));
        crearDia("Hipertrofia - 5 días", 2L, 1, "Espalda y Bíceps", List.of(5L, 6L, 7L, 18L, 19L));
        crearDia("Hipertrofia - 5 días", 2L, 2, "Piernas", List.of(9L, 10L, 11L, 12L, 8L));
        crearDia("Hipertrofia - 5 días", 2L, 3, "Hombros y Core", List.of(14L, 15L, 16L, 17L, 22L));
        crearDia("Hipertrofia - 5 días", 2L, 4, "Full Body y Brazos", List.of(1L, 5L, 9L, 18L, 20L));
        crearDia("Hipertrofia - 5 días", 2L, 5, "", List.of());
        crearDia("Hipertrofia - 5 días", 2L, 6, "", List.of());

        // ==================== PILATES ====================
        crearDia("Pilates Mat - Semana completa", 2L, 0, "Centro y respiración", List.of(37L, 38L, 39L, 45L));
        crearDia("Pilates Mat - Semana completa", 2L, 1, "", List.of());
        crearDia("Pilates Mat - Semana completa", 2L, 2, "Fuerza y estabilidad", List.of(37L, 40L, 42L, 41L));
        crearDia("Pilates Mat - Semana completa", 2L, 3, "", List.of());
        crearDia("Pilates Mat - Semana completa", 2L, 4, "Core avanzado", List.of(37L, 43L, 45L, 39L));
        crearDia("Pilates Mat - Semana completa", 2L, 5, "", List.of());
        crearDia("Pilates Mat - Semana completa", 2L, 6, "Movilidad y estiramiento", List.of(44L, 41L, 40L, 38L));
    }

    /** Crea un día de una rutina pública con sus ejercicios. */
    private void crearDia(String nombreRutina, Long creatorId, int orden, String actividad, List<Long> ejercicios) {
        String dayInfo = actividad.isBlank()
                ? SEMANA[orden]
                : SEMANA[orden] + " - " + actividad;

        Rutina rutina = rutinaRepository.save(Rutina.builder()
                .ownerId(null) // pública
                .creatorId(creatorId)
                .name(nombreRutina)
                .dayInfo(dayInfo)
                .build());

        if (ejercicios.isEmpty()) return;

        List<EjercicioRutina> refs = new java.util.ArrayList<>();
        for (int i = 0; i < ejercicios.size(); i++) {
            refs.add(EjercicioRutina.builder()
                    .routineId(rutina.getId())
                    .exerciseId(ejercicios.get(i))
                    .orderIndex(i + 1)
                    .build());
        }
        ejercicioRutinaRepository.saveAll(refs);
    }
}
