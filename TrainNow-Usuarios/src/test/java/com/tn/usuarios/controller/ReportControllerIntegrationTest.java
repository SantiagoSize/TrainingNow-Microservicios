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
 * Pruebas de integración de reportes de usuarios (H2 en memoria).
 * Crear reporte: cualquier usuario logueado. Listar/resolver: solo admin.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerIntegrationTest {

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

    private String reporteJson(long reporterId, long reportedId, String reason) {
        return """
                {"reporterId": %d, "reporterName": "Test Reporter", "reportedId": %d,
                 "reportedName": "Test Reportado", "reason": "%s"}
                """.formatted(reporterId, reportedId, reason);
    }

    @Test
    void crearReporte_ok() throws Exception {
        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reporteJson(3, 2, "Acoso o lenguaje ofensivo en el chat")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.reason").value("Acoso o lenguaje ofensivo en el chat"));
    }

    @Test
    void crearReporte_sinMotivo_400() throws Exception {
        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reporteJson(3, 2, "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("obligatorio")));
    }

    @Test
    void crearReporte_aSiMismo_400() throws Exception {
        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reporteJson(3, 3, "Sospechoso")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("ti mismo")));
    }

    @Test
    void listarReportes_sinToken_401() throws Exception {
        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarYResolverReportes_comoAdmin_ok() throws Exception {
        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reporteJson(3, 2, "Cuenta bot / registro automatizado")))
                .andExpect(status().isCreated());

        String token = adminToken();
        String listado = mockMvc.perform(get("/api/reports").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").exists())
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) com.jayway.jsonpath.JsonPath.read(listado, "$[0].id")).longValue();

        mockMvc.perform(patch("/api/reports/" + id + "/resolve")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "DISMISSED"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISMISSED"));

        mockMvc.perform(get("/api/reports").param("status", "PENDING").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
