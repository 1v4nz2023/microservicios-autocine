package com.grupo4.autocine.service;

import com.grupo4.autocine.dto.LoginResponseDTO;
import com.grupo4.autocine.dto.UsuarioDTO;
import com.grupo4.autocine.model.Usuario;
import com.grupo4.autocine.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final String DEFAULT_PROFILE_PHOTO = "https://res.cloudinary.com/dbecedl0m/image/upload/v1744610749/profile-photos/1/g7stnpxlra12n18ghitr.png";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<LoginResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToLoginResponseDTO)
                .collect(Collectors.toList());
    }

    public LoginResponseDTO findById(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertToLoginResponseDTO)
                .orElse(null);
    }

    public LoginResponseDTO create(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso");
        }

        Usuario usuario = new Usuario();
        String hashedPassword = passwordEncoder.encode(usuarioDTO.getPassword());
        usuario.setPassword(hashedPassword);
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setFotoPerfil(DEFAULT_PROFILE_PHOTO);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToLoginResponseDTO(savedUsuario);
    }

    public LoginResponseDTO update(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Email cannot be changed through this endpoint
        if (usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().equals(usuario.getEmail())) {
            throw new RuntimeException("Email cannot be changed through this endpoint");
        }

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        
        // Only update password if provided
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(usuarioDTO.getPassword());
            usuario.setPassword(hashedPassword);
        }
        
        // Only update photo if provided
        if (usuarioDTO.getFotoPerfil() != null && !usuarioDTO.getFotoPerfil().isEmpty()) {
            usuario.setFotoPerfil(usuarioDTO.getFotoPerfil());
        }

        return convertToLoginResponseDTO(usuarioRepository.save(usuario));
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void updateProfilePhoto(Long userId, String photoUrl) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setFotoPerfil(photoUrl);
        usuarioRepository.save(usuario);
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