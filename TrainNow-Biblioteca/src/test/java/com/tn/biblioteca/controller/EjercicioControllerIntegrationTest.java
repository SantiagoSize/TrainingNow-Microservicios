package com.tn.biblioteca.controller;

import com.jayway.jsonpath.JsonPath;
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

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del microservicio tn-biblioteca (H2 en memoria).
 */
@SpringBootTest
@AutoConfigureMockMvc
class EjercicioControllerIntegrationTest {

    // Mismo secreto por defecto que JwtValidator (application.properties: jwt.secret).
    // Escribir/editar/borrar ejercicios ahora exige un token ADMIN (JwtValidator.requireAdmin),
    // así que el test arma uno propio en vez de llamar a TrainNow-Usuarios.
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
    void getExercises_devuelveSeed() throws Exception {
        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(10)));
    }

    @Test
    void getByCategory_filtra() throws Exception {
        mockMvc.perform(get("/api/exercises/category/Pectorales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Pectorales"));
    }

    @Test
    void search_porNombre() throws Exception {
        mockMvc.perform(get("/api/exercises/search").param("q", "sentadilla"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sentadilla"));
    }

    @Test
    void crud_completo() throws Exception {
        String nuevo = """
                {"name": "Zancadas", "category": "Piernas", "description": "Zancadas con mancuernas", "isSystemDefault": false}
                """;
        String token = adminToken();
        String location = mockMvc.perform(post("/api/exercises")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Zancadas"))
                .andReturn().getResponse().getContentAsString();

        long id = ((Number) JsonPath.read(location, "$.id")).longValue();

        mockMvc.perform(put("/api/exercises/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Zancadas búlgaras", "category": "Piernas", "isSystemDefault": false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zancadas búlgaras"));

        mockMvc.perform(delete("/api/exercises/" + id)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/exercises/" + id))
                .andExpect(status().isNotFound());
    }
}
