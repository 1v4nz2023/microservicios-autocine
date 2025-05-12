package com.grupo4.autocine.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int duracion;
    private String clasificacion;
    private String idioma;
    private String genero;
    private String formato;
    @Column(length = 1000)
    private String sinopsis;
    
    private String imageUrl;
}
