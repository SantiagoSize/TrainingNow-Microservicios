package com.tn.comunicaciones.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del chat (H2 en memoria).
 */
@SpringBootTest
@AutoConfigureMockMvc
class MensajeControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void conversacion_enviarYLeer() throws Exception {
        // Entrenador (2) escribe al usuario (3)
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senderId": 2, "receiverId": 3, "content": "¡Hola! ¿Listo para entrenar?"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isRead").value(false))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // El usuario responde
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senderId": 3, "receiverId": 2, "content": "¡Listo!"}
                                """))
                .andExpect(status().isCreated());

        // Conversación en ambos sentidos, orden cronológico
        mockMvc.perform(get("/api/messages/conversation/2/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("¡Hola! ¿Listo para entrenar?"));

        // Igual resultado con los ids invertidos
        mockMvc.perform(get("/api/messages/conversation/3/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Mensajes del participante
        mockMvc.perform(get("/api/messages/user/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void marcarLeido() throws Exception {
        String resp = mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senderId": 5, "receiverId": 6, "content": "mensaje"}
                                """))
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) com.jayway.jsonpath.JsonPath.read(resp, "$.id")).longValue();

        mockMvc.perform(patch("/api/messages/" + id + "/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));
    }

    @Test
    void crear_sinContenido_400() throws Exception {
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senderId": 1, "receiverId": 2}
                                """))
                .andExpect(status().isBadRequest());
    }
}
