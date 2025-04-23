package com.ctang.zephyrcentrum.services;

import com.ctang.zephyrcentrum.models.Image;
import com.ctang.zephyrcentrum.repositories.ImageRepository;
import com.ctang.zephyrcentrum.types.Visibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;
    
    /**
     * Retrieves all images associated with a specific user ID.
     *
     * @param userId the ID of the user whose images are to be retrieved
     * @return a list of images belonging to the specified user
     */
    @Override
    public List<Image> getAllImagesByUserId(Long userId) {
        return imageRepository.findByUserId(userId);
    }
    
    /**
     * Retrieves an image by its ID.
     *
     * @param id the ID of the image to retrieve
     * @return an Optional containing the image if found, or empty if not found
     */
    @Override
    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }
    
    /**
     * Stores a single image file in the database.
     *
     * @param file the image file to store
     * @param userId the ID of the user uploading the image
     * @param description a description of the image
     * @return the saved Image entity
     * @throws IOException if there is an error reading the file
     */
    @Override
    public Image storeImage(MultipartFile file, Long userId, String description) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String filename = originalFilename != null ? StringUtils.cleanPath(originalFilename) : "unknown";
        
        Image image = new Image();
        image.setName(filename);
        image.setContentType(file.getContentType());
        image.setData(file.getBytes());
        image.setSize(file.getSize());
        image.setDescription(description);
        image.setUserId(userId);
        image.setUploadDate(LocalDateTime.now());
        image.setVisibility(Visibility.PRIVATE); // Default visibility set to PRIVATE
        
        return imageRepository.save(image);
    }

    /**
     * Sets the visibility of an image.
     *
     * @param imageId the ID of the image to set the visibility of
     * @param visibility the visibility to set the image to
     * @return true if the image was found and updated, false otherwise
     */
    @Override
    public Image updateImage(Long imageId, Image image) {
        Optional<Image> imageOptional = imageRepository.findById(imageId);
        if (imageOptional.isPresent()) {
            Image imageToUpdate = imageOptional.get();
            if (image.getUserId() != imageToUpdate.getUserId()) {
                throw new IllegalArgumentException("User does not have permission to update this image");
            }
            imageToUpdate.setName(image.getName());
            imageToUpdate.setContentType(image.getContentType());
            imageToUpdate.setData(image.getData());
            imageToUpdate.setSize(image.getSize());
            imageToUpdate.setDescription(image.getDescription());
            imageToUpdate.setVisibility(image.getVisibility());
            imageRepository.save(imageToUpdate);
            return imageToUpdate;
        }
        return null;
    }

    /**
     * Stores multiple image files in the database.
     *
     * @param files an array of image files to store
     * @param userId the ID of the user uploading the images
     * @return a list of saved Image entities
     * @throws IOException if there is an error reading any of the files
     */
    @Override
    public List<Image> storeMultipleImages(MultipartFile[] files, Long userId) throws IOException {
        List<Image> savedImages = new ArrayList<>();
        
        for (MultipartFile file : files) {
            savedImages.add(storeImage(file, userId, null));
        }
        
        return savedImages;
    }
    
    /**
     * Deletes an image from the database by its ID.
     *
     * @param imageId the ID of the image to delete
     * @return true if the image was found and deleted, false otherwise
     */
    @Override
    public boolean deleteImage(Long imageId) {
        if (imageRepository.existsById(imageId)) {
            imageRepository.deleteById(imageId);
            return true;
        }
        return false;
    }
}