package com.tn.biblioteca.config;

import com.tn.biblioteca.model.*;
import com.tn.biblioteca.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null") // Evita avisos de conversión con Iterable<@NonNull Ejercicio> en saveAll
public class BibliotecaDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaDataLoader.class);

    private final EjercicioRepository ejercicioRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (ejercicioRepository.count() > 0) {
            log.debug("Biblioteca ya tiene ejercicios cargados");
            return;
        }
        final List<Ejercicio> ejercicios = buildEjerciciosIniciales();
        ejercicioRepository.saveAll(ejercicios);
        log.info("Cargados {} ejercicios en la biblioteca", ejercicios.size());
    }

    private List<Ejercicio> buildEjerciciosIniciales() {
        return List.of(
                Ejercicio.builder()
                        .nombre("Press de banca")
                        .descripcion("Ejercicio compuesto para pecho. Tumbado en banco, bajar la barra al pecho y extender.")
                        .grupoMuscular(GrupoMuscular.PECHO)
                        .dificultad(Dificultad.INTERMEDIO)
                        .equipamiento(Equipamiento.BARRA)
                        .urlVideo("https://example.com/press-banca")
                        .caloriasEstimadas(8.5)
                        .build(),
                Ejercicio.builder()
                        .nombre("Sentadilla con barra")
                        .descripcion("Ejercicio fundamental de piernas. Barra sobre hombros, flexionar rodillas y cadera.")
                        .grupoMuscular(GrupoMuscular.PIERNAS)
                        .dificultad(Dificultad.INTERMEDIO)
                        .equipamiento(Equipamiento.BARRA)
                        .urlVideo("https://example.com/sentadilla")
                        .caloriasEstimadas(10.0)
                        .build(),
                Ejercicio.builder()
                        .nombre("Dominadas")
                        .descripcion("Tirar del cuerpo hacia la barra hasta que la barbilla la supere. Trabaja espalda y brazos.")
                        .grupoMuscular(GrupoMuscular.ESPALDA)
                        .dificultad(Dificultad.AVANZADO)
                        .equipamiento(Equipamiento.PESO_CORPORAL)
                        .urlVideo("https://example.com/dominadas")
                        .caloriasEstimadas(9.0)
                        .build(),
                Ejercicio.builder()
                        .nombre("Press militar con mancuernas")
                        .descripcion("Press de hombros sentado o de pie con mancuernas.")
                        .grupoMuscular(GrupoMuscular.HOMBROS)
                        .dificultad(Dificultad.PRINCIPIANTE)
                        .equipamiento(Equipamiento.MANCUERNAS)
                        .urlVideo("https://example.com/press-militar")
                        .caloriasEstimadas(6.0)
                        .build(),
                Ejercicio.builder()
                        .nombre("Curl de bíceps con barra")
                        .descripcion("Flexión de codos con barra para aislar bíceps.")
                        .grupoMuscular(GrupoMuscular.BRAZOS)
                        .dificultad(Dificultad.PRINCIPIANTE)
                        .equipamiento(Equipamiento.BARRA)
                        .urlVideo("https://example.com/curl-biceps")
                        .caloriasEstimadas(4.0)
                        .build(),
                Ejercicio.builder()
                        .nombre("Plancha abdominal")
                        .descripcion("Mantener posición de plancha apoyado en antebrazos y puntas de los pies. Trabaja core.")
                        .grupoMuscular(GrupoMuscular.CORE)
                        .dificultad(Dificultad.PRINCIPIANTE)
                        .equipamiento(Equipamiento.PESO_CORPORAL)
                        .urlVideo("https://example.com/plancha")
                        .caloriasEstimadas(5.0)
                        .build(),
                Ejercicio.builder()
                        .nombre("Remo con mancuerna")
                        .descripcion("Apoyado en banco, tirar de la mancuerna hacia la cadera. Trabaja espalda.")
                        .grupoMuscular(GrupoMuscular.ESPALDA)
                        .dificultad(Dificultad.INTERMEDIO)
                        .equipamiento(Equipamiento.MANCUERNAS)
                        .urlVideo("https://example.com/remo-mancuerna")
                        .caloriasEstimadas(7.0)
                        .build(),
                Ejercicio.builder()
                        .nombre("Prensa de piernas")
                        .descripcion("En máquina, empujar la plataforma con los pies. Cuádriceps e isquiotibiales.")
                        .grupoMuscular(GrupoMuscular.PIERNAS)
                        .dificultad(Dificultad.PRINCIPIANTE)
                        .equipamiento(Equipamiento.MAQUINA)
                        .urlVideo("https://example.com/prensa")
                        .caloriasEstimadas(8.0)
                        .build(),
                Ejercicio.builder()
                        .nombre("Fondos en paralelas")
                        .descripcion("Descender y subir el cuerpo entre dos barras paralelas. Pecho y tríceps.")
                        .grupoMuscular(GrupoMuscular.PECHO)
                        .dificultad(Dificultad.INTERMEDIO)
                        .equipamiento(Equipamiento.PESO_CORPORAL)
                        .urlVideo("https://example.com/fondos")
                        .caloriasEstimadas(7.5)
                        .build(),
                Ejercicio.builder()
                        .nombre("Elevaciones laterales en máquina")
                        .descripcion("Elevar los brazos lateralmente en máquina para aislar deltoides lateral.")
                        .grupoMuscular(GrupoMuscular.HOMBROS)
                        .dificultad(Dificultad.PRINCIPIANTE)
                        .equipamiento(Equipamiento.MAQUINA)
                        .urlVideo("https://example.com/elevaciones-laterales")
                        .caloriasEstimadas(4.5)
                        .build()
        );
    }
}
