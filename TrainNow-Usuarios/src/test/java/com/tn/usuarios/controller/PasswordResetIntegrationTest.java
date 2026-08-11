package com.tn.usuarios.controller;

import com.tn.usuarios.repository.PasswordResetCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas del flujo completo de recuperación de contraseña (EmailJS en modo dev).
 */
@SpringBootTest
@AutoConfigureMockMvc
class PasswordResetIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private PasswordResetCodeRepository codeRepository;

    @Test
    void flujoCompleto_requestVerifyConfirm_yLoginConNuevaPassword() throws Exception {
        // 1. Solicitar código (usuario seed usuario@gmail.com)
        mockMvc.perform(post("/api/users/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "usuario@gmail.com"}
                                """))
                .andExpect(status().isOk());

        String code = codeRepository
                .findTopByEmailIgnoreCaseAndUsedFalseOrderByCreatedAtDesc("usuario@gmail.com")
                .orElseThrow().getCode();

        // 2. Verificar código
        mockMvc.perform(post("/api/users/password-reset/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "usuario@gmail.com", "code": "%s"}
                                """.formatted(code)))
                .andExpect(status().isOk());

        // 3. Confirmar nueva contraseña
        mockMvc.perform(post("/api/users/password-reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "usuario@gmail.com", "code": "%s", "newPassword": "nueva1234"}
                                """.formatted(code)))
                .andExpect(status().isOk());

        // 4. Login con la nueva contraseña
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "usuario@gmail.com", "password": "nueva1234"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("usuario@gmail.com"));

        // 5. El código ya no puede reutilizarse
        mockMvc.perform(post("/api/users/password-reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "usuario@gmail.com", "code": "%s", "newPassword": "otra1234"}
                                """.formatted(code)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void request_emailInexistente_404() throws Exception {
        mockMvc.perform(post("/api/users/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "noexiste@nada.tn"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void verify_codigoIncorrecto_400() throws Exception {
        mockMvc.perform(post("/api/users/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "entrenador@trainingnow.com"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/users/password-reset/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "entrenador@trainingnow.com", "code": "000000"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
