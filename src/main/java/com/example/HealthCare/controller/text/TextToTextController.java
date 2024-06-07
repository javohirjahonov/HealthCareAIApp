package com.example.HealthCare.controller.text;

import com.example.HealthCare.domain.dto.request.text.TextRequestDto;
import com.example.HealthCare.service.TextToTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/text-to-text")
@RequiredArgsConstructor
@Slf4j // Logback for logging
public class TextToTextController {

    private final TextToTextService textToTextService;

    @PostMapping("/generate-text")
    public ResponseEntity<String> generateText(
            @RequestBody TextRequestDto requestDto // Using DTO to encapsulate the text input
    ) {
        log.info("Received request to generate text with input: {}", requestDto.getText());
        try {
            String generatedText = textToTextService.generateTextToText(requestDto.getText());
            return ResponseEntity.ok(generatedText);
        } catch (Exception e) {
            log.error("Error generating text: ", e);
            return ResponseEntity.internalServerError().body("Error generating text");
        }
    }
}
