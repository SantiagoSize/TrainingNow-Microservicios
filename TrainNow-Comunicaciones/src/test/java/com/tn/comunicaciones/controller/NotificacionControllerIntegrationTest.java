package com.tn.comunicaciones.controller;

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
 * Pruebas de integración del microservicio tn-comunicaciones (H2 en memoria).
 */
@SpringBootTest
@AutoConfigureMockMvc
class NotificacionControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void getByUser_devuelveSeedBienvenida() throws Exception {
        mockMvc.perform(get("/api/notifications/user/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    void crear_marcarLeida_yEliminar() throws Exception {
        String notif = """
                {"userId": 5, "title": "Nueva rutina asignada", "message": "Tu coach te asignó una rutina",
                 "type": "ROUTINE", "priority": "HIGH", "senderId": 2}
                """;
        String resp = mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notif))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isRead").value(false))
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(resp, "$.id")).longValue();

        mockMvc.perform(patch("/api/notifications/" + id + "/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));

        mockMvc.perform(delete("/api/notifications/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notifications/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_sinTitulo_400() throws Exception {
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": 5, "message": "sin titulo"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
