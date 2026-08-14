package com.tn.comunicaciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Sirve los adjuntos de chat (imágenes/videos) subidos a disco como recursos estáticos,
 * para que la app Android pueda descargarlos por URL sin pasar por la base de datos.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/chat}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rutaAbsoluta = new File(uploadDir).getAbsolutePath();
        registry.addResourceHandler("/uploads/chat/**")
                .addResourceLocations("file:" + rutaAbsoluta + File.separator);
    }
}
