package com.grupo4.autocine.controller;

import com.grupo4.autocine.dto.ErrorResponseDTO;
import com.grupo4.autocine.dto.LoginDTO;
import com.grupo4.autocine.dto.LoginResponseDTO;
import com.grupo4.autocine.exception.AuthenticationException;
import com.grupo4.autocine.model.Usuario;
import com.grupo4.autocine.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Usuario usuario = authService.authenticate(loginDTO);
            return ResponseEntity.ok(mapToLoginResponse(usuario));
        } catch (AuthenticationException e) {
            ErrorResponseDTO error = new ErrorResponseDTO("Usuario y/o contraseña incorrectos", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    private LoginResponseDTO mapToLoginResponse(Usuario usuario) {
        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setEmail(usuario.getEmail());
        return response;
    }
}