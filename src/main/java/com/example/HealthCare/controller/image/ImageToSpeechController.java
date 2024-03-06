package com.example.HealthCare.controller.image;

import com.example.HealthCare.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageToSpeechController {

    private final ImageService imageService;

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }
        String prompt = "Analyse this photo ";
        String result = imageService.generateImageDescription(file, prompt);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/generate-speech")
    public ResponseEntity<String> generateSpeech(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        try {
            String prompt = "Analyse this photo";
            String audioFilePath = "output.wav";
            String generatedText = imageService.generateImageDescription(file, prompt);
            ImageService.convertTextToSpeech(generatedText, audioFilePath);
            ImageService.saveTextToFile(generatedText, "output.txt");
            return ResponseEntity.ok().body("Speech generated successfully");
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate speech");
        }
    }
}
