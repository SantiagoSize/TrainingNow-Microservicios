package com.tn.usuarios.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lee el header "Authorization: Bearer &lt;token&gt;" en cada request y, si el token es
 * válido, publica un Authentication en el SecurityContext con la autoridad ROLE_&lt;rol&gt;
 * (rol viene del claim del propio JWT, emitido por JwtService en el login).
 *
 * A propósito NUNCA lanza una excepción ni corta la cadena: si el token falta o es inválido,
 * simplemente no autentica y deja que authorizeHttpRequests() decida (401/403 solo en las
 * rutas que de verdad lo exigen; el resto de la API sigue abierta como hasta ahora).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                JwtService.TokenClaims claims = jwtService.fromAuthHeader(authHeader);
                var authentication = new UsernamePasswordAuthenticationToken(
                        claims.userId(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + claims.role()))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ex) {
                // Token presente pero inválido/expirado: se ignora, la request sigue como
                // anónima. Si la ruta exige un rol, authorizeHttpRequests() la va a rechazar.
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
