package com.grupo4.autocine.controller;

import com.grupo4.autocine.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file, folder);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Imagen subida exitosamente");
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Error al subir la imagen: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/profile/{userId}")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadProfilePhoto(file, userId);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Foto de perfil subida exitosamente");
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Error al subir la foto de perfil: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
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
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Error al eliminar la imagen: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 