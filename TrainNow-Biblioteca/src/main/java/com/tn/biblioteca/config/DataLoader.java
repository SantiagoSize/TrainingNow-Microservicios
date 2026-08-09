package com.tn.biblioteca.config;

import com.tn.biblioteca.model.Ejercicio;
import com.tn.biblioteca.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Seed inicial de la biblioteca de ejercicios (solo si la tabla está vacía). */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EjercicioRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        repository.saveAll(List.of(
                ej("Press de banca", "Pectorales", "Press con barra en banco plano, agarre medio."),
                ej("Press inclinado con mancuernas", "Pectorales", "Press en banco inclinado 30°, foco en pectoral superior."),
                ej("Aperturas con mancuernas", "Pectorales", "Aperturas en banco plano con ligera flexión de codos."),
                ej("Dominadas", "Espalda", "Dominadas pronas al ancho de hombros."),
                ej("Remo con barra", "Espalda", "Remo inclinado con barra, espalda neutra."),
                ej("Jalón al pecho", "Espalda", "Jalón en polea alta, agarre abierto."),
                ej("Sentadilla", "Piernas", "Sentadilla libre con barra alta, profundidad completa."),
                ej("Prensa de piernas", "Piernas", "Prensa 45°, pies al ancho de caderas."),
                ej("Peso muerto rumano", "Piernas", "RDL con barra, foco en isquiotibiales."),
                ej("Press militar", "Hombros", "Press de pie con barra, core firme."),
                ej("Elevaciones laterales", "Hombros", "Elevaciones con mancuernas hasta la horizontal."),
                ej("Curl con barra", "Bíceps", "Curl estricto con barra recta."),
                ej("Fondos en paralelas", "Tríceps", "Fondos con torso vertical, foco en tríceps."),
                ej("Plancha", "Core", "Plancha abdominal isométrica."),
                ej("Crunch abdominal", "Core", "Crunch en suelo, repeticiones controladas.")
        ));
    }

    private Ejercicio ej(String name, String category, String description) {
        return Ejercicio.builder()
                .name(name)
                .category(category)
                .description(description)
                .isSystemDefault(true)
                .build();
    }
}
