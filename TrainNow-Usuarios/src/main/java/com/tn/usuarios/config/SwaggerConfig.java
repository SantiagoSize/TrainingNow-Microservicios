package com.tn.usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("TrainNow-Usuarios")
                .description("Microservicio de usuarios, autenticación y relaciones entrenador-cliente")
                .version("1.0"));
    }
}
