package com.tn.rutinas.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas del registro de asistencia y del reporte mensual (H2 en memoria).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AttendanceIntegrationTest {

    @Autowired private MockMvc mockMvc;

    private void registrar(long userId, String date, String status, int ejercicios, int minutos) throws Exception {
        mockMvc.perform(post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": %d, "date": "%s", "status": "%s",
                                 "exercisesCompleted": %d, "durationMinutes": %d}
                                """.formatted(userId, date, status, ejercicios, minutos)))
                .andExpect(status().isCreated());
    }

    @Test
    void reporteMensual_calculaAdherenciaYRachas() throws Exception {
        long userId = 77L;
        registrar(userId, "2026-08-03", "TRAINED", 5, 60);
        registrar(userId, "2026-08-04", "TRAINED", 6, 55);
        registrar(userId, "2026-08-05", "MISSED", 0, 0);
        registrar(userId, "2026-08-06", "REST", 0, 0);
        registrar(userId, "2026-08-07", "TRAINED", 4, 45);
        registrar(userId, "2026-08-08", "TRAINED", 5, 50);

        mockMvc.perform(get("/api/attendance/user/" + userId + "/report/2026-08"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daysTrained").value(4))
                .andExpect(jsonPath("$.daysMissed").value(1))
                .andExpect(jsonPath("$.daysRest").value(1))
                .andExpect(jsonPath("$.totalExercises").value(20))
                .andExpect(jsonPath("$.totalMinutes").value(210))
                .andExpect(jsonPath("$.adherencePercent").value(80))
                .andExpect(jsonPath("$.currentStreak").value(2))
                .andExpect(jsonPath("$.days.length()").value(6));
    }

    @Test
    void registro_esIdempotentePorDia() throws Exception {
        long userId = 78L;
        registrar(userId, "2026-08-10", "TRAINED", 3, 30);
        registrar(userId, "2026-08-10", "TRAINED", 7, 70); // mismo día: actualiza

        mockMvc.perform(get("/api/attendance/user/" + userId + "/report/2026-08"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daysTrained").value(1))
                .andExpect(jsonPath("$.totalExercises").value(7));
    }

    @Test
    void mesSinDatos_devuelveCeros() throws Exception {
        mockMvc.perform(get("/api/attendance/user/999/report/2026-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daysTrained").value(0))
                .andExpect(jsonPath("$.adherencePercent").value(0));
    }
}
