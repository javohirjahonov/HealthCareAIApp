package com.example.HealthCare.controller.image;

import com.example.HealthCare.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageToTextController {

    private final ImageService imageService;

    @PostMapping("/generate-description")
    public ResponseEntity<String> generateDescription(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        try {
            String prompt = "Analyze the file ";
            String generatedDescription = imageService.generateImageDescription(file, prompt);
            return ResponseEntity.ok().body(generatedDescription);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate image description");
        }
    }
}
