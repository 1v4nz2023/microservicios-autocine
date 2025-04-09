package com.grupo4.autocine.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;

} 