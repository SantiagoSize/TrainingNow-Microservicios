package com.tn.rutinas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lee el header "Authorization: Bearer &lt;token&gt;" (mismo secreto compartido que
 * TrainNow-Usuarios) y, si es válido, publica un Authentication en el SecurityContext con
 * ROLE_&lt;rol&gt;. TrainNow-Rutinas no tiene hoy ningún endpoint restringido por rol (todo
 * sigue permitAll en SecurityConfig), este filtro solo deja la infraestructura lista por si
 * en el futuro se quiere restringir algo, sin cambiar el comportamiento actual.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Pattern ROLE = Pattern.compile("\"role\":\"([A-Z]+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");

    @Value("${jwt.secret:trainingnow-secret-dev-2026-cambiar-en-produccion}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String role = validarYObtenerRol(authHeader.substring(7).trim());
                var authentication = new UsernamePasswordAuthenticationToken(
                        "jwt-user", null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ex) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String validarYObtenerRol(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) {
            throw new IllegalArgumentException("Token inválido");
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Matcher exp = EXP.matcher(payload);
        if (!exp.find() || Long.parseLong(exp.group(1)) < System.currentTimeMillis()) {
            throw new IllegalArgumentException("Token expirado");
        }
        Matcher role = ROLE.matcher(payload);
        if (!role.find()) {
            throw new IllegalArgumentException("Token inválido");
        }
        return role.group(1);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Error validando token", e);
        }
    }
}
