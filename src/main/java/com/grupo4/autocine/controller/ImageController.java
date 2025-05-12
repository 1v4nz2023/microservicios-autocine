package com.grupo4.autocine.controller;

import com.grupo4.autocine.dto.PeliculaDTO;
import com.grupo4.autocine.service.CloudinaryService;
import com.grupo4.autocine.service.UsuarioService;
import com.grupo4.autocine.service.PeliculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Images", description = "Image management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ImageController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/png", "image/gif"};

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PeliculaService peliculaService;

    @PostMapping("/upload")
    @Operation(summary = "Upload image", description = "Uploads an image to Cloudinary")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or upload failed")
    })
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        try {
            validateFile(file);
            String imageUrl = cloudinaryService.uploadImage(file, folder);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Imagen subida exitosamente");
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/profile/{userId}")
    @Operation(summary = "Upload profile photo", description = "Uploads a profile photo for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile photo uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or upload failed"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            validateFile(file);
            String imageUrl = cloudinaryService.uploadProfilePhoto(file, userId);
            
            usuarioService.updateProfilePhoto(userId, imageUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Foto de perfil subida exitosamente");
            response.put("imageUrl", imageUrl);
            response.put("user", usuarioService.findById(userId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete image", description = "Deletes an image from Cloudinary")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Deletion failed")
    })
    public ResponseEntity<?> deleteImage(@RequestParam String publicId) {
        try {
            cloudinaryService.deleteImage(publicId);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Imagen eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/peliculas/{peliculaId}")
    @Operation(summary = "Upload movie image", description = "Uploads an image for a movie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or upload failed"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<?> uploadMovieImage(
            @PathVariable Long peliculaId,
            @RequestParam("file") MultipartFile file) {
        try {
            validateFile(file);
            String imageUrl = cloudinaryService.uploadMovieImage(file, peliculaId);
            
            peliculaService.updateImage(peliculaId, imageUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Imagen de película subida exitosamente");
            response.put("imageUrl", imageUrl);
            response.put("pelicula", peliculaService.findById(peliculaId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/peliculas/nueva")
    @Operation(summary = "Subir imagen y crear película", description = "Sube una imagen y crea una nueva película. Flujo simplificado para frontends.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Película creada con imagen exitosamente"),
        @ApiResponse(responseCode = "400", description = "Archivo inválido o datos inválidos")
    })
    public ResponseEntity<?> uploadImageAndCreatePelicula(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nombre") String nombre,
            @RequestParam("duracion") int duracion,
            @RequestParam("clasificacion") String clasificacion,
            @RequestParam("idioma") String idioma,
            @RequestParam("genero") String genero,
            @RequestParam("formato") String formato,
            @RequestParam(value = "sinopsis", required = false) String sinopsis) {
        try {
            validateFile(file);
            
            // Upload image first
            String imageUrl = cloudinaryService.uploadImage(file, "autocine/peliculas/nueva");
            
            // Create movie DTO
            PeliculaDTO peliculaDTO = new PeliculaDTO();
            peliculaDTO.setNombre(nombre);
            peliculaDTO.setDuracion(duracion);
            peliculaDTO.setClasificacion(clasificacion);
            peliculaDTO.setIdioma(idioma);
            peliculaDTO.setGenero(genero);
            peliculaDTO.setFormato(formato);
            peliculaDTO.setSinopsis(sinopsis);
            peliculaDTO.setImageUrl(imageUrl);
            
            // Create movie
            PeliculaDTO createdPelicula = peliculaService.create(peliculaDTO);
            
            // If movie is created successfully, move the image to the correct folder
            if (createdPelicula.getId() != null) {
                String finalImageUrl = cloudinaryService.uploadMovieImage(file, createdPelicula.getId());
                peliculaService.updateImage(createdPelicula.getId(), finalImageUrl);
                createdPelicula.setImageUrl(finalImageUrl);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Película creada con imagen exitosamente");
            response.put("pelicula", createdPelicula);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("El archivo excede el tamaño máximo permitido (5MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedFileType(contentType)) {
            throw new RuntimeException("Tipo de archivo no permitido. Solo se aceptan JPEG, PNG y GIF");
        }
    }

    private boolean isAllowedFileType(String contentType) {
        for (String allowedType : ALLOWED_FILE_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        return error;
    }
} 