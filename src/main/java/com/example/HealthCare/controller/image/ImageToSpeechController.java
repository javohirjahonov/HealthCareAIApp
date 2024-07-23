package com.example.HealthCare.controller.image;

import com.example.HealthCare.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.example.HealthCare.service.ImageService.convertTextToSpeech;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageToSpeechController {

    private final ImageService imageService;

    @Operation(summary = "Method for upload file")
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }
        try {
            String prompt = "Analyse this photo";
            String result = imageService.generateImageToText(file, prompt);
            return ResponseEntity.ok().body(result);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    @Operation(summary = "Method for upload file")
    @PostMapping(value = "/generate-speech", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateSpeech(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        try {
            String prompt = "Analyse this photo";
            String basePath = "D:\\OWN INFORMATONS\\GIT PROJECTS\\Health Care AI\\src\\main\\resources\\voice_data";
            String generatedText = imageService.generateImageToSpeech(file, prompt);
            convertTextToSpeech(generatedText, basePath+ "\\output.wav");
            ImageService.saveTextToFile(generatedText, basePath);
            return ResponseEntity.ok().body("Speech generated successfully");
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate speech");
        }
    }
}