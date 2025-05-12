package com.grupo4.autocine.repository;

import com.grupo4.autocine.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByNombreContainingIgnoreCase(String nombre);
    List<Pelicula> findByGenero(String genero);
    List<Pelicula> findByFormato(String formato);
} 