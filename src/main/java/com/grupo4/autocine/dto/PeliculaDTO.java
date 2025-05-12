package com.grupo4.autocine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PeliculaDTO {
    @Schema(description = "ID de la película (no requerido para creación)", required = false)
    private Long id;

    @Schema(description = "Nombre de la película", required = true)
    private String nombre;

    @Schema(description = "Duración de la película en minutos", required = true)
    private int duracion;

    @Schema(description = "Clasificación de la película (G, PG, PG-13, R, etc.)", required = true)
    private String clasificacion;

    @Schema(description = "Idioma de la película", required = true)
    private String idioma;

    @Schema(description = "Género de la película", required = true)
    private String genero;

    @Schema(description = "Formato de la película (2D, 3D, IMAX, etc.)", required = true)
    private String formato;

    @Schema(description = "Sinopsis de la película", required = false)
    private String sinopsis;
    
    @Schema(description = "URL de la imagen de la película", required = true)
    private String imageUrl;
} 