package com.tn.usuarios.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Seguridad para entorno académico/local, con JWT real (ver JwtService / JwtAuthenticationFilter):
 * - La mayoría de la API sigue abierta (permitAll), igual que antes.
 * - Los endpoints administrativos (ban/suspender usuarios, audit-logs, reportes, crear staff)
 *   ahora exigen además un token JWT de rol ADMIN a nivel de Spring Security, no solo el
 *   chequeo manual que ya existía en UserService.requireActiveAdmin (ese se mantiene como
 *   segunda capa: también valida que el admin no esté baneado/suspendido).
 * - CSRF deshabilitado (API REST stateless consumida por la app móvil), sesión STATELESS.
 * - Passwords siempre hasheadas con BCrypt.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] RUTAS_ADMIN = {
            "/api/users/admin-create",
            "/api/users/*/ban",
            "/api/users/*/unban",
            "/api/users/*/suspend",
            "/api/users/*/unsuspend",
            "/api/audit-logs",
            "/api/audit-logs/**",
            "/api/reports/*/resolve"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(RUTAS_ADMIN).hasRole("ADMIN")
                    // GET /api/reports también es solo-admin (listar reportes), pero
                    // POST /api/reports (crear un reporte) es libre para cualquier usuario logueado.
                    .requestMatchers(HttpMethod.GET, "/api/reports").hasRole("ADMIN")
                    // DELETE /api/users/{id} es solo-admin, pero PUT /api/users/{id} (editar el
                    // propio perfil) sigue abierto, por eso va aparte y no en RUTAS_ADMIN.
                    .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("ADMIN")
                    .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(unauthorizedEntryPoint())
                    .accessDeniedHandler(forbiddenHandler())
            );
        return http.build();
    }

    /**
     * 401 con el mismo formato {"error": "..."} que usa GlobalExceptionHandler para el resto
     * de la API. JSON armado a mano (sin ObjectMapper inyectado): spring-boot-starter-webmvc
     * en Spring Boot 4.1 no deja un bean ObjectMapper disponible para autowire directo aquí.
     */
    private AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Falta el token de autorización\"}");
        };
    }

    /** 403 con el mismo formato {"error": "..."} para cuando el token es válido pero el rol no alcanza. */
    private AccessDeniedHandler forbiddenHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Operación permitida solo para administradores\"}");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
