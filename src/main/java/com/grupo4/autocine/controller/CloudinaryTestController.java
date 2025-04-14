package com.grupo4.autocine.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class CloudinaryTestController {

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/cloudinary")
    public ResponseEntity<?> testCloudinaryConnection() {
        try {
            // Test connection by getting cloudinary account details
            Map<?, ?> result = cloudinary.api().ping(ObjectUtils.emptyMap());
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Conexión exitosa con Cloudinary",
                "details", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error al conectar con Cloudinary",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/cloudinary/upload")
    public ResponseEntity<?> testUpload() {
        try {
            // Upload a test image
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                ObjectUtils.asMap(
                    "folder", "test-uploads",
                    "public_id", "test-image"
                )
            );
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Imagen subida exitosamente",
                "url", uploadResult.get("secure_url")
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error al subir imagen",
                "error", e.getMessage()
            ));
        }
    }
} 