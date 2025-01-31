package org.experiment.experimentalproject.controllers;

import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.experiment.experimentalproject.client.AzureImageStorageClient;
import org.experiment.experimentalproject.client.ImageStorageClient;
import org.experiment.experimentalproject.entities.Image;
import org.experiment.experimentalproject.service.ImageService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.Result;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageStorageClient imageStorageClient;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (!(fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png"))) {
            return ResponseEntity.badRequest().body("Invalid file format. Only JPG and PNG are allowed.");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File size should not exceed 10MB.");
        }

        try {
            Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setPath(path.toString());
            imageService.saveImage(image);

            return ResponseEntity.ok().body("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Image with this name already exists.");
        }
    }

    @GetMapping
    public ResponseEntity<?> listImages() {
        return ResponseEntity.ok().body(imageService.getAllImages());
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadImage(@RequestParam String containerName, @RequestParam MultipartFile file) throws IOException {
        try(InputStream inputStream = file.getInputStream()) {
            String imageUrl = this.imageStorageClient.downloadImage(containerName, file.getOriginalFilename(), inputStream, file.getSize());
            return ResponseEntity.ok().body(imageUrl);
        }
    }
}
