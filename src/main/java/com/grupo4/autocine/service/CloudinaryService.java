package com.grupo4.autocine.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final String DEFAULT_PROFILE_PHOTO = "https://res.cloudinary.com/dbecedl0m/image/upload/v1713031232/autocine/profile/default-profile.png";

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto"
                ));
        return (String) uploadResult.get("secure_url");
    }

    public String uploadProfilePhoto(MultipartFile file, Long userId) throws IOException {
        // Delete old profile photo if it exists and is not the default one
        String oldPhotoUrl = getCurrentProfilePhoto(userId);
        if (oldPhotoUrl != null && !oldPhotoUrl.equals(DEFAULT_PROFILE_PHOTO)) {
            deleteOldProfilePhoto(oldPhotoUrl);
        }

        // Upload new photo
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "autocine/profile/" + userId,
                        "public_id", "profile_" + userId,
                        "resource_type", "image"
                ));
        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private void deleteOldProfilePhoto(String photoUrl) {
        try {
            // Extract public_id from the URL
            String publicId = extractPublicIdFromUrl(photoUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            // Log the error but don't throw it - we don't want to fail the upload if deletion fails
            System.err.println("Error deleting old profile photo: " + e.getMessage());
        }
    }

    private String extractPublicIdFromUrl(String url) {
        try {
            // Cloudinary URLs typically have the public_id after the last slash
            String[] parts = url.split("/");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                // Remove file extension if present
                return lastPart.split("\\.")[0];
            }
        } catch (Exception e) {
            System.err.println("Error extracting public_id from URL: " + e.getMessage());
        }
        return null;
    }

    private String getCurrentProfilePhoto(Long userId) {
        // This method would typically call your user service to get the current photo URL
        // For now, we'll return null as we'll get the URL from the user service
        return null;
    }
} 