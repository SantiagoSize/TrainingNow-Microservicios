package com.tn.biblioteca.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del microservicio tn-biblioteca (H2 en memoria).
 */
@SpringBootTest
@AutoConfigureMockMvc
class EjercicioControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;

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
        String location = mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Zancadas"))
                .andReturn().getResponse().getContentAsString();

        long id = ((Number) JsonPath.read(location, "$.id")).longValue();

        mockMvc.perform(put("/api/exercises/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Zancadas búlgaras", "category": "Piernas", "isSystemDefault": false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zancadas búlgaras"));

        mockMvc.perform(delete("/api/exercises/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/exercises/" + id))
                .andExpect(status().isNotFound());
    }
}
