package com.grupo4.autocine.service;

import com.grupo4.autocine.dto.LoginDTO;
import com.grupo4.autocine.dto.LoginResponseDTO;
import com.grupo4.autocine.exception.AuthenticationException;
import com.grupo4.autocine.model.Usuario;
import com.grupo4.autocine.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public LoginResponseDTO login(LoginDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("Usuario y/o contraseña incorrecta"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())) {
            throw new AuthenticationException("Usuario y/o contraseña incorrecta");
        }

        LoginResponseDTO response = convertToLoginResponseDTO(usuario);
        response.setToken(jwtService.generateToken(response));
        return response;
    }

    private LoginResponseDTO convertToLoginResponseDTO(Usuario usuario) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        return dto;
    }
} 