package com.example.HealthCare.controller.text;

import com.example.HealthCare.service.ImageService;
import com.example.HealthCare.service.TextToTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/text-to-speech")
@RequiredArgsConstructor
public class TextToSpeechController {
    private final TextToTextService textToText;
//    @PostMapping("/generate-text-to-speech")
//    public String generateSpeech(
//            @RequestBody String text
//    ) {
//        return textToText.generateSpeech(text);
//    }

    @PostMapping("/generate-text-to-speech")
    public ResponseEntity<String> generateSpeech(
            @RequestBody String text
    ) {
        if (text.isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide text to convert to speech");
        }

        try {
            String audioFilePath = "output2.wav";
            // Generate text-to-speech using the service
            String response = textToText.generateContent(text);
            TextToTextService.convertTextToSpeech(response, audioFilePath);
            return ResponseEntity.ok().body("Speech generated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate speech");
        }
    }
}
