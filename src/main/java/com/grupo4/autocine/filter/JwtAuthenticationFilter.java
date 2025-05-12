package com.grupo4.autocine.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo4.autocine.dto.ErrorResponseDTO;
import com.grupo4.autocine.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, "No token provided");
            return;
        }

        try {
            jwt = authHeader.substring(7);
            if (!jwtService.validateToken(jwt)) {
                sendErrorResponse(response, "Invalid token");
                return;
            }

            userEmail = jwtService.extractClaim(jwt, claims -> claims.get("email", String.class));
            String userRole = jwtService.extractClaim(jwt, claims -> claims.get("rol", String.class));

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Crear autoridades basadas en el rol
                List<SimpleGrantedAuthority> authorities = Collections.emptyList();
                if (userRole != null && !userRole.isEmpty()) {
                    authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole));
                }
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userEmail, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendErrorResponse(response, "Invalid token format");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ErrorResponseDTO error = new ErrorResponseDTO(message, HttpStatus.UNAUTHORIZED.value());
        objectMapper.writeValue(response.getWriter(), error);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();
        
        // Permitir rutas públicas sin autenticación
        return path.contains("/api/auth/login") || 
               path.contains("/api/auth/forgot-password") ||
               path.contains("/api/auth/reset-password") ||
               (path.equals("/api/usuarios") && method.equals("POST")) ||
               (path.startsWith("/api/peliculas") && method.equals("GET")) ||
               path.startsWith("/v3/api-docs") || 
               path.startsWith("/swagger-ui");
    }
} 