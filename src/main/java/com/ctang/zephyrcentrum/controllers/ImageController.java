package com.ctang.zephyrcentrum.controllers;

import com.ctang.zephyrcentrum.models.Image;
import com.ctang.zephyrcentrum.services.ImageServiceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/images")
@CrossOrigin(maxAge = 3600)
public class ImageController {

    private final ImageServiceImpl imageService;

    public ImageController(ImageServiceImpl imageService) {
        this.imageService = imageService;
    }
    
    /**
     * Get all images for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Image>> getAllImagesByUser(@PathVariable Long userId) {
        List<Image> images = imageService.getAllImagesByUserId(userId);
        return ResponseEntity.ok(images);
    }
    
    /**
     * Get image by ID
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<Image> getImageById(@PathVariable Long imageId) {
        return imageService.getImageById(imageId)
            .map(image -> ResponseEntity.ok()
                .body(image))
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{imageId}")
    public ResponseEntity<Image> updateImage(@PathVariable Long imageId, @RequestBody Image image) {
        Image updatedImage = imageService.updateImage(imageId, image);
        return ResponseEntity.ok(updatedImage);
    }

    /**
     * Upload one or multiple images
     * Handles both single and multiple file uploads in one endpoint
     */
    @PostMapping("/upload")
    public ResponseEntity<List<Image>> uploadImages(
            @RequestParam MultipartFile[] files,
            @RequestParam Long userId,
            @RequestParam(required = false) String description) {
        try {
            if (files.length == 1) {
                // Single file upload
                Image savedImage = imageService.storeImage(files[0], userId, description);
                List<Image> savedImages = new ArrayList<>();
                savedImages.add(savedImage);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedImages);
            } else {
                // Multiple file upload
                List<Image> savedImages = imageService.storeMultipleImages(files, userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedImages);
            }
        } catch (IOException e) {
            // Return an empty list instead of a string message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ArrayList<>());
        }
    }

    /**
     * Delete an image
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        if (imageService.deleteImage(imageId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
