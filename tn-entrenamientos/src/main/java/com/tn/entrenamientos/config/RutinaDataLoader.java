package com.tn.entrenamientos.config;

import com.tn.entrenamientos.dto.RutinaEjercicioRequestDTO;
import com.tn.entrenamientos.dto.RutinaRequestDTO;
import com.tn.entrenamientos.dto.SesionEntrenamientoRequestDTO;
import com.tn.entrenamientos.service.RutinaService;
import com.tn.entrenamientos.service.SesionEntrenamientoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RutinaDataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(RutinaDataLoader.class);

    private final RutinaService rutinaService;
    private final SesionEntrenamientoService sesionEntrenamientoService;

    @Bean
    public CommandLineRunner loadRutinasData() {
        return args -> {
            LOGGER.info("Cargando datos de ejemplo para tn-entrenamientos...");

            // Rutina 1 - Full body para Santiago (userId=1)
            RutinaEjercicioRequestDTO r1e1 = new RutinaEjercicioRequestDTO();
            r1e1.setEjercicioId(1L); // Press banca
            r1e1.setSeries(3);
            r1e1.setRepeticiones(10);
            r1e1.setDescanso(90);
            r1e1.setObservaciones("Calentamiento ligero, foco en tÃ©cnica.");

            RutinaEjercicioRequestDTO r1e2 = new RutinaEjercicioRequestDTO();
            r1e2.setEjercicioId(2L); // Sentadilla
            r1e2.setSeries(4);
            r1e2.setRepeticiones(8);
            r1e2.setDescanso(120);
            r1e2.setObservaciones("No llegar al fallo, mantener espalda neutra.");

            RutinaRequestDTO rutinaSantiago = new RutinaRequestDTO();
            rutinaSantiago.setOwnerId(1L); // Santiago Serrano
            rutinaSantiago.setCreatorId(2L); // coach Lucia
            rutinaSantiago.setNombre("Full Body Progresivo - Santiago");
            rutinaSantiago.setDescripcion("Rutina full body enfocada en ganancia de fuerza para Santiago Serrano.");
            rutinaSantiago.setEjercicios(List.of(r1e1, r1e2));

            rutinaService.crearRutina(rutinaSantiago);

            // Rutina 2 - Empuje para otro atleta
            RutinaEjercicioRequestDTO r2e1 = new RutinaEjercicioRequestDTO();
            r2e1.setEjercicioId(1L);
            r2e1.setSeries(4);
            r2e1.setRepeticiones(12);
            r2e1.setDescanso(90);
            r2e1.setObservaciones("Subir peso si la Ãºltima serie es cÃ³moda.");

            RutinaRequestDTO rutinaEmpuje = new RutinaRequestDTO();
            rutinaEmpuje.setOwnerId(3L); // otro atleta
            rutinaEmpuje.setCreatorId(2L);
            rutinaEmpuje.setNombre("DÃ­a de Empuje - Hipertrofia");
            rutinaEmpuje.setDescripcion("Pecho, hombros y trÃ­ceps para atleta intermedio.");
            rutinaEmpuje.setEjercicios(List.of(r2e1));

            rutinaService.crearRutina(rutinaEmpuje);

            // Crear algunas sesiones de ejemplo para Santiago (userId=1).
            // Para simplificar, asumimos que los RutinaEjercicio creados tienen IDs 1 y 2.
            SesionEntrenamientoRequestDTO sesion1 = new SesionEntrenamientoRequestDTO();
            sesion1.setUserId(1L);
            sesion1.setRutinaEjercicioId(1L);
            sesion1.setSeriesRealizadas(3);
            sesion1.setRepeticionesPorSerie(10);
            sesion1.setPesoLevantado(70.0);
            sesionEntrenamientoService.registrarSesion(sesion1);

            SesionEntrenamientoRequestDTO sesion2 = new SesionEntrenamientoRequestDTO();
            sesion2.setUserId(1L);
            sesion2.setRutinaEjercicioId(2L);
            sesion2.setSeriesRealizadas(4);
            sesion2.setRepeticionesPorSerie(8);
            sesion2.setPesoLevantado(90.0);
            sesionEntrenamientoService.registrarSesion(sesion2);

            LOGGER.info("Datos de ejemplo de rutinas y sesiones creados correctamente.");
        };
    }
}
