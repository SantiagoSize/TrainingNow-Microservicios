package com.tn.biblioteca.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración de categorías de la biblioteca (H2 en memoria).
 * Crear/renombrar/eliminar exige token ADMIN (JwtValidator.requireAdmin).
 */
@SpringBootTest
@AutoConfigureMockMvc
class CategoriaControllerIntegrationTest {

    // Mismo secreto por defecto que JwtValidator (application.properties: jwt.secret).
    private static final String JWT_SECRET = "trainingnow-secret-dev-2026-cambiar-en-produccion";

    @Autowired private MockMvc mockMvc;

    private String adminToken() throws Exception {
        String headerB64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        long exp = System.currentTimeMillis() + 60_000L;
        String payloadJson = "{\"sub\":\"1\",\"role\":\"ADMIN\",\"email\":\"admin@trainingnow.com\",\"exp\":" + exp + "}";
        String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(JWT_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal((headerB64 + "." + payloadB64).getBytes(StandardCharsets.UTF_8)));
        return "Bearer " + headerB64 + "." + payloadB64 + "." + signature;
    }

    @Test
    void getAll_devuelveSeed() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void crear_sinToken_401() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Cardio"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crud_completo() throws Exception {
        String token = adminToken();

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Movilidad"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Movilidad"));

        mockMvc.perform(put("/api/categories/Movilidad")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "MovilidadAvanzada"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MovilidadAvanzada"));

        mockMvc.perform(delete("/api/categories/MovilidadAvanzada")
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void crear_nombreDuplicado_403() throws Exception {
        String token = adminToken();
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Pectorales"}
                                """))
                .andExpect(status().isForbidden());
    }
}
