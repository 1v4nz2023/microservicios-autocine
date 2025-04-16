package com.grupo4.autocine.controller;

import com.grupo4.autocine.dto.ErrorResponseDTO;
import com.grupo4.autocine.dto.LoginResponseDTO;
import com.grupo4.autocine.dto.UsuarioDTO;
import com.grupo4.autocine.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<LoginResponseDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoginResponseDTO> findById(@PathVariable Long id) {
        LoginResponseDTO usuario = usuarioService.findById(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            LoginResponseDTO response = usuarioService.create(usuarioDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoginResponseDTO> update(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.update(id, usuarioDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 