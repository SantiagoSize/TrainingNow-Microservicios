package com.tn.entrenamientos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tn.entrenamientos.dto.RutinaEjercicioRequestDTO;
import com.tn.entrenamientos.dto.RutinaRequestDTO;
import com.tn.entrenamientos.dto.SesionEntrenamientoRequestDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
class RutinaAndSesionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearRutinaYRegistrarSesion_debeResponderOk() throws Exception {
        // Crear rutina sencilla
        RutinaEjercicioRequestDTO ejercicio = new RutinaEjercicioRequestDTO();
        ejercicio.setEjercicioId(1L);
        ejercicio.setSeries(3);
        ejercicio.setRepeticiones(10);
        ejercicio.setDescanso(90);

        RutinaRequestDTO rutina = new RutinaRequestDTO();
        rutina.setOwnerId(1L);
        rutina.setCreatorId(2L);
        rutina.setNombre("Rutina de prueba");
        rutina.setDescripcion("Creada desde test de integración");
        rutina.setEjercicios(List.of(ejercicio));

        String rutinaJson = objectMapper.writeValueAsString(rutina);

        mockMvc.perform(post("/api/routines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rutinaJson))
                .andExpect(status().isCreated());

        // Registrar sesión de entrenamiento asociada a algún ejercicio (id=1 a modo de ejemplo)
        SesionEntrenamientoRequestDTO sesion = new SesionEntrenamientoRequestDTO();
        sesion.setUserId(1L);
        sesion.setRutinaEjercicioId(1L);
        sesion.setSeriesRealizadas(3);
        sesion.setRepeticionesPorSerie(10);
        sesion.setPesoLevantado(60.0);

        String sesionJson = objectMapper.writeValueAsString(sesion);

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sesionJson))
                .andExpect(status().isCreated());

        // Consultar sesiones del usuario
        mockMvc.perform(get("/api/workouts/user/{userId}", 1L))
                .andExpect(status().isOk());
    }
}
