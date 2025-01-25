package org.experiment.experimentalproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.experiment.experimentalproject.repositories.ImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.experiment.experimentalproject.entities.Image;
import org.springframework.web.client.HttpServerErrorException;

import java.sql.SQLException;
import java.util.*;


@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }
}
