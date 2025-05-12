package com.grupo4.autocine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo4.autocine.dto.ErrorResponseDTO;
import com.grupo4.autocine.dto.PeliculaDTO;
import com.grupo4.autocine.exception.PeliculaNotFoundException;
import com.grupo4.autocine.service.CloudinaryService;
import com.grupo4.autocine.service.PeliculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
@Tag(name = "Películas", description = "API para gestionar películas")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    @Operation(summary = "Obtener todas las películas", description = "Retorna una lista de todas las películas disponibles")
    public ResponseEntity<List<PeliculaDTO>> findAll() {
        return ResponseEntity.ok(peliculaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener película por ID", description = "Retorna una película basada en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Película encontrada"),
            @ApiResponse(responseCode = "404", description = "Película no encontrada")
    })
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(peliculaService.findById(id));
        } catch (PeliculaNotFoundException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar películas por nombre", description = "Retorna una lista de películas que coinciden con el nombre proporcionado")
    public ResponseEntity<List<PeliculaDTO>> findByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(peliculaService.findByNombre(nombre));
    }

    @GetMapping("/genero/{genero}")
    @Operation(summary = "Buscar películas por género", description = "Retorna una lista de películas de un género específico")
    public ResponseEntity<List<PeliculaDTO>> findByGenero(@PathVariable String genero) {
        return ResponseEntity.ok(peliculaService.findByGenero(genero));
    }

    @GetMapping("/formato/{formato}")
    @Operation(summary = "Buscar películas por formato", description = "Retorna una lista de películas de un formato específico")
    public ResponseEntity<List<PeliculaDTO>> findByFormato(@PathVariable String formato) {
        return ResponseEntity.ok(peliculaService.findByFormato(formato));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Crear una nueva película", 
        description = "Crea una nueva película enviando los datos como JSON y la imagen como archivo. El campo 'pelicula' debe contener un objeto JSON con los datos de la película."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Película creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error al procesar la imagen")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @Parameter(
                description = "Objeto JSON con los datos de la película", 
                schema = @Schema(implementation = PeliculaDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de película",
                        value = "{\n" +
                               "  \"nombre\": \"Avatar\",\n" +
                               "  \"duracion\": 162,\n" +
                               "  \"clasificacion\": \"PG-13\",\n" +
                               "  \"idioma\": \"Inglés\",\n" +
                               "  \"genero\": \"Ciencia ficción\",\n" +
                               "  \"formato\": \"3D\",\n" +
                               "  \"sinopsis\": \"En un exuberante planeta llamado Pandora...\"\n" +
                               "}"
                    )
                }
            )
            @RequestParam("pelicula") String peliculaJson) {
        
        try {
            // Convertir el JSON a objeto PeliculaDTO
            ObjectMapper objectMapper = new ObjectMapper();
            PeliculaDTO peliculaDTO;
            try {
                peliculaDTO = objectMapper.readValue(peliculaJson, PeliculaDTO.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar los datos de la película: " + e.getMessage());
            }
            
            // Si hay imagen, procesarla
            if (imagen != null && !imagen.isEmpty()) {
                // Subir imagen temporal
                String imageUrl = cloudinaryService.uploadImage(imagen, "autocine/peliculas/temp");
                peliculaDTO.setImageUrl(imageUrl);
            }
            
            // Validar que tenga una URL de imagen (proporcionada en el JSON o subida)
            if (peliculaDTO.getImageUrl() == null || peliculaDTO.getImageUrl().isEmpty()) {
                throw new RuntimeException("Es obligatorio proporcionar una imagen o una URL de imagen");
            }
            
            // Crear la película
            PeliculaDTO createdPelicula = peliculaService.create(peliculaDTO);
            
            // Si se creó exitosamente y hay imagen, mover la imagen a la carpeta definitiva
            if (createdPelicula.getId() != null && imagen != null && !imagen.isEmpty()) {
                String finalImageUrl = cloudinaryService.uploadMovieImage(imagen, createdPelicula.getId());
                peliculaService.updateImage(createdPelicula.getId(), finalImageUrl);
                createdPelicula.setImageUrl(finalImageUrl);
            }
            
            return new ResponseEntity<>(createdPelicula, HttpStatus.CREATED);
            
        } catch (IOException e) {
            ErrorResponseDTO error = new ErrorResponseDTO("Error al subir la imagen: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una película existente", description = "Actualiza los datos de una película existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Película actualizada"),
            @ApiResponse(responseCode = "404", description = "Película no encontrada")
    })
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PeliculaDTO peliculaDTO) {
        try {
            return ResponseEntity.ok(peliculaService.update(id, peliculaDTO));
        } catch (PeliculaNotFoundException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @PutMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar solo la imagen de una película", description = "Actualiza la imagen de una película existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al procesar la imagen"),
        @ApiResponse(responseCode = "404", description = "Película no encontrada")
    })
    public ResponseEntity<?> updateImage(@PathVariable Long id, @RequestParam("imagen") MultipartFile imagen) {
        try {
            if (imagen.isEmpty()) {
                throw new RuntimeException("La imagen es obligatoria");
            }
            
            // Verificar si la película existe
            peliculaService.findById(id);
            
            // Subir la nueva imagen
            String imageUrl = cloudinaryService.uploadMovieImage(imagen, id);
            
            // Actualizar la película con la nueva URL de imagen
            peliculaService.updateImage(id, imageUrl);
            
            return ResponseEntity.ok(peliculaService.findById(id));
            
        } catch (PeliculaNotFoundException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IOException e) {
            ErrorResponseDTO error = new ErrorResponseDTO("Error al subir la imagen: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una película", description = "Elimina una película basada en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Película eliminada"),
            @ApiResponse(responseCode = "404", description = "Película no encontrada")
    })
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            peliculaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (PeliculaNotFoundException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
} 