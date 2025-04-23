package com.ctang.zephyrcentrum.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.ctang.zephyrcentrum.models.Image;

public interface ImageService {
    public List<Image> getAllImagesByUserId(Long userId);
    public Optional<Image> getImageById(Long id);
    public Image storeImage(MultipartFile file, Long userId, String description) throws IOException;
    public Image updateImage(Long imageId, Image image);
    public List<Image> storeMultipleImages(MultipartFile[] files, Long userId) throws IOException;
    public boolean deleteImage(Long imageId);
}
