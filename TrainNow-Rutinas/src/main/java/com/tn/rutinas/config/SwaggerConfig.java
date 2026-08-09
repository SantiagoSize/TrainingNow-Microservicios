package com.tn.rutinas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("TrainNow-Rutinas")
                .description("Microservicio de rutinas y sesiones de entrenamiento")
                .version("1.0"));
    }
}
