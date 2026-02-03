package com.tn.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tn.usuarios.dto.LoginRequestDTO;
import com.tn.usuarios.dto.RegisterRequestDTO;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldReturnCreated() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("test+" + System.currentTimeMillis() + "@trainingnow.com");
        request.setPassword("Password123!");
        request.setNombre("Test");
        request.setApellidos("User");
        request.setTelefono("+34600111222");
        request.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        request.setRol("CLIENT");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void login_shouldReturnUnauthorizedForUnknownUser() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("unknown@trainingnow.com");
        request.setPassword("whatever");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }
}
