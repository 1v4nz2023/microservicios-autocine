package com.grupo4.autocine.controller;

import com.grupo4.autocine.service.CloudinaryService;
import com.grupo4.autocine.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/png", "image/gif"};

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/upload")
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
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            validateFile(file);
            String imageUrl = cloudinaryService.uploadProfilePhoto(file, userId);
            
            // Update user's profile photo in database
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