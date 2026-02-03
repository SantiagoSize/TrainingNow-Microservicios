package com.tn.usuarios.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración global de OpenAPI (Swagger) para la API de identidad y perfil.
 * El acceso a /v3/api-docs y /swagger-ui/** permanece público (permitAll en SecurityConfig).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI trainingNowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TrainingNow - API de Identidad y Perfil")
                        .version("1.1")
                        .description(
                                "Servicio centralizado para la gestión de usuarios, autenticación JWT y perfiles antropométricos del proyecto TrainingNow. " +
                                "Incluye registro, login y consulta de perfil con datos personales (nombre, apellidos, fecha de nacimiento, género), " +
                                "físicos (peso, altura) y objetivo de entrenamiento (pérdida de peso, ganar músculo, mantener, etc.)."
                        ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtenido mediante POST /api/auth/login o POST /api/auth/register")));
    }
}
