package com.tn.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tn.usuarios.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del microservicio tn-usuarios (H2 en memoria).
 * Verifica el contrato consumido por la app Android.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private UserDto nuevoUsuario(String email) {
        return UserDto.builder()
                .role("USER")
                .name("Test")
                .lastName("Integracion")
                .email(email)
                .phone("+56911111111")
                .password("test1234")
                .build();
    }

    @Test
    void getUsers_devuelveSeed() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").exists());
    }

    @Test
    void crearUsuario_yLogin_ok() throws Exception {
        String email = "nuevo@test.tn";
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario(email))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.id").isNumber());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", "test1234"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void login_credencialesInvalidas_401() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "admin@admin.tn", "password", "incorrecta"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearUsuario_emailDuplicado_409() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario("dup@test.tn"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario("dup@test.tn"))))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserById_inexistente_404() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTrainers_soloRolTrainer() throws Exception {
        mockMvc.perform(get("/api/users/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("TRAINER"));
    }

    @Test
    void trainerClients_crearYConsultar() throws Exception {
        String body = """
                {"trainerId": 2, "clientId": 3, "status": "ACTIVE", "sessionsPerWeek": 4}
                """;
        mockMvc.perform(post("/api/trainer-clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/trainer-clients/trainer/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value(3));

        mockMvc.perform(get("/api/trainer-clients/trainer/2/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionsPerWeek").value(4));
    }
}
