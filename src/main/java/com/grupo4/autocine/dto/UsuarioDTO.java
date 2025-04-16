package com.grupo4.autocine.dto;

import com.grupo4.autocine.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UsuarioDTO {
    @Schema(description = "ID del usuario (no requerido para creación)", required = false)
    private Long id;

    @Schema(description = "Nombre del usuario", required = true)
    private String nombre;

    @Schema(description = "Apellido del usuario", required = true)
    private String apellido;

    @Schema(description = "Email del usuario", required = true)
    private String email;

    @Schema(description = "Contraseña del usuario", required = true)
    private String password;

    @Schema(description = "URL de la foto de perfil", required = false)
    private String fotoPerfil;

    @Schema(description = "Rol del usuario (ADMIN, USER, EMPLOYEE). Por defecto es USER", required = false, defaultValue = "USER")
    private Usuario.Rol rol;
} 