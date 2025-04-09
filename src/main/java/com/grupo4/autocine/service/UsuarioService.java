package com.grupo4.autocine.service;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO findById(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public UsuarioDTO create(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Usuario usuario = new Usuario();
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());

        return convertToDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO update(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) && 
            usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());

        return convertToDTO(usuarioRepository.save(usuario));
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    public UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        return dto;
    }
} 