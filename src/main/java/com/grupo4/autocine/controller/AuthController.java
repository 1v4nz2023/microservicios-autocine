package com.grupo4.autocine.controller;

import com.grupo4.autocine.dto.ErrorResponseDTO;
import com.grupo4.autocine.dto.LoginDTO;
import com.grupo4.autocine.dto.LoginResponseDTO;
import com.grupo4.autocine.exception.AuthenticationException;
import com.grupo4.autocine.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = authService.login(loginDTO);
            
            // Create the cookie
            ResponseCookie cookie = ResponseCookie.from("user_data", response.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60) // 24 hours
                .build();

            // Return the response with the cookie
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
        } catch (AuthenticationException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO("User not authenticated", HttpStatus.UNAUTHORIZED.value()));
        }

        // Create an expired cookie to clear the existing one
        ResponseCookie cookie = ResponseCookie.from("user_data", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0) // Expire immediately
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Map.of("message", "Logged out successfully"));
    }
}