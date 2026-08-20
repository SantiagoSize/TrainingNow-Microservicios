package com.tn.usuarios.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del registro de actividad administrativa (H2 en memoria).
 * Guardar y listar exigen token de un ADMIN activo (UserService.requireActiveAdmin).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuditLogControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;

    private String adminToken() throws Exception {
        String resp = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "admin@trainingnow.com", "password": "Admin123"}
                                """))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + com.jayway.jsonpath.JsonPath.read(resp, "$.token");
    }

    private String logJson(String targetType, String targetName) {
        return """
                {"actorId": 1, "actorName": "Admin TrainingNow", "actorRole": "ADMIN",
                 "action": "EXERCISE_CREATED", "targetType": "%s", "targetId": 99,
                 "targetName": "%s", "details": "Creado desde test de integración"}
                """.formatted(targetType, targetName);
    }

    @Test
    void registrar_sinToken_401() throws Exception {
        mockMvc.perform(post("/api/audit-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logJson("EXERCISE", "Zancadas")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listar_sinToken_401() throws Exception {
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrarYListar_comoAdmin_ok() throws Exception {
        String token = adminToken();

        mockMvc.perform(post("/api/audit-logs")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logJson("EXERCISE", "Zancadas búlgaras")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.action").value("EXERCISE_CREATED"))
                .andExpect(jsonPath("$.actorRole").value("ADMIN"))
                .andExpect(jsonPath("$.id").isNumber());

        mockMvc.perform(get("/api/audit-logs").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").exists());
    }

    @Test
    void listarFiltradoPorTargetType_comoAdmin_ok() throws Exception {
        String token = adminToken();

        mockMvc.perform(post("/api/audit-logs")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logJson("CATEGORY", "Piernas")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/audit-logs").param("targetType", "CATEGORY").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].targetType").value("CATEGORY"));
    }

    @Test
    void registrar_comoUsuarioNormal_403() throws Exception {
        String resp = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "usuario@gmail.com", "password": "User1234"}
                                """))
                .andReturn().getResponse().getContentAsString();
        String userToken = "Bearer " + com.jayway.jsonpath.JsonPath.read(resp, "$.token");

        mockMvc.perform(post("/api/audit-logs")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logJson("EXERCISE", "Zancadas")))
                .andExpect(status().isForbidden());
    }
}
