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
 * Pruebas de integración del microservicio TrainNow-Usuarios (H2 en memoria).
 * Verifica el contrato consumido por la app Android.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;

    private String adminToken() throws Exception {
        String resp = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "admin@trainingnow.com", "password": "Admin123"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + com.jayway.jsonpath.JsonPath.read(resp, "$.token");
    }

    private String nuevoUsuarioJson(String email) {
        return """
                {"role": "USER", "name": "Test", "lastName": "Integracion",
                 "email": "%s", "phone": "+56911111111", "password": "test1234"}
                """.formatted(email);
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
                        .content(nuevoUsuarioJson(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.id").isNumber());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "test1234"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_credencialesInvalidas_401() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "admin@trainingnow.com", "password": "incorrecta"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearUsuario_emailDuplicado_409() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevoUsuarioJson("dup@test.tn")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevoUsuarioJson("dup@test.tn")))
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
        mockMvc.perform(post("/api/trainer-clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"trainerId": 2, "clientId": 3, "status": "ACTIVE", "sessionsPerWeek": 4}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/trainer-clients/trainer/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value(3));

        mockMvc.perform(get("/api/trainer-clients/trainer/2/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionsPerWeek").value(4));
    }

    // ==================== Seguridad: creación de usuarios ====================

    @Test
    void registroPublico_conRolAdmin_seFuerzaAUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": "ADMIN", "name": "Intruso", "email": "intruso@gmail.com", "password": "hack1234"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER")); // rol forzado, sin privilegios
    }

    @Test
    void registroPublico_conDominioCorporativo_403() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": "USER", "name": "Falso", "email": "falso@trainingnow.com", "password": "hack1234"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCreate_porAdmin_creaEntrenador() throws Exception {
        mockMvc.perform(post("/api/users/admin-create")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": "TRAINER", "name": "Nuevo", "lastName": "Entrenador",
                                 "email": "nuevo.entrenador@trainingnow.com", "password": "entrena123",
                                 "specializations": "CrossFit"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("TRAINER"))
                .andExpect(jsonPath("$.email").value("nuevo.entrenador@trainingnow.com"));
    }

    @Test
    void adminCreate_porUsuarioNormal_403() throws Exception {
        // login del usuario normal para obtener SU token (sin privilegios)
        String resp = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "usuario@gmail.com", "password": "User1234"}
                                """))
                .andReturn().getResponse().getContentAsString();
        String userToken = "Bearer " + com.jayway.jsonpath.JsonPath.read(resp, "$.token");

        mockMvc.perform(post("/api/users/admin-create")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": "ADMIN", "name": "Escalada", "email": "escalada@trainingnow.com", "password": "hack1234"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCreate_staffSinDominioCorporativo_400() throws Exception {
        mockMvc.perform(post("/api/users/admin-create")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": "TRAINER", "name": "Mal", "email": "mal@gmail.com",
                                 "password": "entrena123", "specializations": "Fuerza"}
                                """))
                .andExpect(status().isBadRequest());
    }

    // ==================== Sanciones ====================

    @Test
    void banear_bloqueaLogin_yLevantarLoPermite() throws Exception {
        String token = adminToken();
        // crear víctima
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevoUsuarioJson("baneado@test.tn")))
                .andExpect(status().isCreated());
        String resp = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "baneado@test.tn", "password": "test1234"}
                                """))
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) com.jayway.jsonpath.JsonPath.read(resp, "$.id")).longValue();

        // ban
        mockMvc.perform(patch("/api/users/" + id + "/ban")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason": "Conducta inapropiada"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isBanned").value(true));

        // login bloqueado con mensaje
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "baneado@test.tn", "password": "test1234"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("baneada")));

        // unban → login funciona
        mockMvc.perform(patch("/api/users/" + id + "/unban").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isBanned").value(false));
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "baneado@test.tn", "password": "test1234"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void suspender_bloqueaLogin_conFechaYMotivo() throws Exception {
        String token = adminToken();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevoUsuarioJson("suspendido@test.tn")))
                .andExpect(status().isCreated());
        String resp = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "suspendido@test.tn", "password": "test1234"}
                                """))
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) com.jayway.jsonpath.JsonPath.read(resp, "$.id")).longValue();
        long hastaManana = System.currentTimeMillis() + 24 * 60 * 60 * 1000L;

        mockMvc.perform(patch("/api/users/" + id + "/suspend")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"untilMillis": %d, "reason": "Spam en chats"}
                                """.formatted(hastaManana)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "suspendido@test.tn", "password": "test1234"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("suspendida")));

        mockMvc.perform(patch("/api/users/" + id + "/unsuspend").header("Authorization", token))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "suspendido@test.tn", "password": "test1234"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void sancionar_sinToken_401() throws Exception {
        mockMvc.perform(patch("/api/users/3/ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason": "x"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void banear_sinMotivo_400() throws Exception {
        String token = adminToken();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevoUsuarioJson("sinmotivo.ban@test.tn")))
                .andExpect(status().isCreated());
        long id = idDeUsuario("sinmotivo.ban@test.tn", "test1234");

        mockMvc.perform(patch("/api/users/" + id + "/ban")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reason": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("obligatorio")));
    }

    @Test
    void suspender_sinMotivo_400() throws Exception {
        String token = adminToken();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevoUsuarioJson("sinmotivo.susp@test.tn")))
                .andExpect(status().isCreated());
        long id = idDeUsuario("sinmotivo.susp@test.tn", "test1234");
        long hastaManana = System.currentTimeMillis() + 24 * 60 * 60 * 1000L;

        mockMvc.perform(patch("/api/users/" + id + "/suspend")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"untilMillis": %d, "reason": ""}
                                """.formatted(hastaManana)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("obligatorio")));
    }

    /** Login con [email]/[password] y devuelve el id del usuario autenticado. */
    private long idDeUsuario(String email, String password) throws Exception {
        String resp = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s"}
                                """.formatted(email, password)))
                .andReturn().getResponse().getContentAsString();
        return ((Number) com.jayway.jsonpath.JsonPath.read(resp, "$.id")).longValue();
    }
}
