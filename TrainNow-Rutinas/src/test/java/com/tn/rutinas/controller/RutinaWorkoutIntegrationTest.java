package com.tn.rutinas.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del microservicio tn-rutinas: rutinas + workouts (H2 en memoria).
 */
@SpringBootTest
@AutoConfigureMockMvc
class RutinaWorkoutIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void rutinasPublicas_devuelveSeed() throws Exception {
        // El seed de rutinas públicas es Básquetbol/Hipertrofia/Pilates (ver DataLoader);
        // "Full Body Principiante" era el seed anterior a la tarea #29.
        mockMvc.perform(get("/api/routines/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pretemporada de Básquetbol"));
    }

    @Test
    void crearRutina_asignarEjercicios_yConsultar() throws Exception {
        String rutina = """
                {"ownerId": 3, "creatorId": 2, "name": "Push Pull Legs", "dayInfo": "Lunes, Martes, Jueves"}
                """;
        String resp = mockMvc.perform(post("/api/routines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rutina))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Push Pull Legs"))
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(resp, "$.id")).longValue();

        String ejercicios = """
                [{"routineId": %d, "exerciseId": 1, "order": 0},
                 {"routineId": %d, "exerciseId": 7, "order": 1}]
                """.formatted(id, id);
        mockMvc.perform(post("/api/routines/" + id + "/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ejercicios))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/routines/" + id + "/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].order").value(0));

        mockMvc.perform(get("/api/routines/owner/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerId").value(3));
    }

    @Test
    void workout_sesionYLogs_flujoCompleto() throws Exception {
        String sesion = """
                {"userId": 3, "routineId": 1, "status": "IN_PROGRESS", "location": "Gimnasio Peñalolén"}
                """;
        String resp = mockMvc.perform(post("/api/workouts/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sesion))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andReturn().getResponse().getContentAsString();
        long sessionId = ((Number) JsonPath.read(resp, "$.id")).longValue();

        String log = """
                {"sessionId": %d, "exerciseId": 1, "orderInSession": 0,
                 "plannedSets": 4, "plannedReps": 10, "completedSets": 4,
                 "actualReps": "10,10,9,8", "weightKg": 60.0, "rpe": 8}
                """.formatted(sessionId);
        mockMvc.perform(post("/api/workouts/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(log))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.actualReps").value("10,10,9,8"));

        mockMvc.perform(get("/api/workouts/sessions/" + sessionId + "/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        String cierre = """
                {"userId": 3, "routineId": 1, "status": "COMPLETED", "totalDurationMinutes": 55, "rating": 5}
                """;
        mockMvc.perform(put("/api/workouts/sessions/" + sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cierre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(get("/api/workouts/sessions/user/3/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalDurationMinutes").value(55));
    }

    @Test
    void sesionInexistente_404() throws Exception {
        mockMvc.perform(get("/api/workouts/sessions/99999"))
                .andExpect(status().isNotFound());
    }
}
