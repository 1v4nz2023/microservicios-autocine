package com.grupo4.autocine.service;

import com.grupo4.autocine.dto.LoginDTO;
import com.grupo4.autocine.exception.AuthenticationException;
import com.grupo4.autocine.model.Usuario;
import com.grupo4.autocine.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario authenticate(LoginDTO loginDTO) {
        if (loginDTO == null || loginDTO.getEmail() == null || loginDTO.getPassword() == null) {
            throw new AuthenticationException("Email and password are required");
        }

        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        return usuario;
    }
} 