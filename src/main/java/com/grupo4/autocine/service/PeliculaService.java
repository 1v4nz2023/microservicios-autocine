package com.grupo4.autocine.service;

import com.grupo4.autocine.dto.PeliculaDTO;
import com.grupo4.autocine.exception.PeliculaNotFoundException;
import com.grupo4.autocine.model.Pelicula;
import com.grupo4.autocine.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    public List<PeliculaDTO> findAll() {
        return peliculaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PeliculaDTO findById(Long id) {
        return peliculaRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new PeliculaNotFoundException(id));
    }

    public List<PeliculaDTO> findByNombre(String nombre) {
        return peliculaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PeliculaDTO> findByGenero(String genero) {
        return peliculaRepository.findByGenero(genero).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PeliculaDTO> findByFormato(String formato) {
        return peliculaRepository.findByFormato(formato).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PeliculaDTO create(PeliculaDTO peliculaDTO) {
        Pelicula pelicula = convertToEntity(peliculaDTO);
        pelicula.setId(null); // Ensure we're creating a new entity
        
        // Validate that image URL is provided
        if (pelicula.getImageUrl() == null || pelicula.getImageUrl().isEmpty()) {
            throw new RuntimeException("La URL de la imagen es obligatoria para crear una película");
        }
        
        Pelicula savedPelicula = peliculaRepository.save(pelicula);
        return convertToDTO(savedPelicula);
    }

    public PeliculaDTO update(Long id, PeliculaDTO peliculaDTO) {
        Pelicula existingPelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new PeliculaNotFoundException(id));
        
        Pelicula pelicula = convertToEntity(peliculaDTO);
        pelicula.setId(id);
        
        // Keep the current image if not provided in the update
        if (pelicula.getImageUrl() == null || pelicula.getImageUrl().isEmpty()) {
            pelicula.setImageUrl(existingPelicula.getImageUrl());
        }
        
        Pelicula updatedPelicula = peliculaRepository.save(pelicula);
        return convertToDTO(updatedPelicula);
    }

    public void delete(Long id) {
        if (!peliculaRepository.existsById(id)) {
            throw new PeliculaNotFoundException(id);
        }
        peliculaRepository.deleteById(id);
    }
    
    public void updateImage(Long peliculaId, String imageUrl) {
        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(() -> new PeliculaNotFoundException(peliculaId));
        
        pelicula.setImageUrl(imageUrl);
        peliculaRepository.save(pelicula);
    }

    private PeliculaDTO convertToDTO(Pelicula pelicula) {
        PeliculaDTO dto = new PeliculaDTO();
        dto.setId(pelicula.getId());
        dto.setNombre(pelicula.getNombre());
        dto.setDuracion(pelicula.getDuracion());
        dto.setClasificacion(pelicula.getClasificacion());
        dto.setIdioma(pelicula.getIdioma());
        dto.setGenero(pelicula.getGenero());
        dto.setFormato(pelicula.getFormato());
        dto.setSinopsis(pelicula.getSinopsis());
        dto.setImageUrl(pelicula.getImageUrl());
        return dto;
    }

    private Pelicula convertToEntity(PeliculaDTO peliculaDTO) {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(peliculaDTO.getId());
        pelicula.setNombre(peliculaDTO.getNombre());
        pelicula.setDuracion(peliculaDTO.getDuracion());
        pelicula.setClasificacion(peliculaDTO.getClasificacion());
        pelicula.setIdioma(peliculaDTO.getIdioma());
        pelicula.setGenero(peliculaDTO.getGenero());
        pelicula.setFormato(peliculaDTO.getFormato());
        pelicula.setSinopsis(peliculaDTO.getSinopsis());
        pelicula.setImageUrl(peliculaDTO.getImageUrl());
        return pelicula;
    }
} 