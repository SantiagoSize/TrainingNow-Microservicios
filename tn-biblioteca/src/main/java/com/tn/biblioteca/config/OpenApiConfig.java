package com.tn.biblioteca.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TrainingNow - Biblioteca de Ejercicios",
                version = "1.0",
                description = "Catálogo de ejercicios para consulta de entrenadores y atletas."
        )
)
public class OpenApiConfig {
}
