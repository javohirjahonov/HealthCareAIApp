package com.example.HealthCare.controller.text;

import com.example.HealthCare.service.TextToTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/text-to-text")
@RequiredArgsConstructor
public class TextToTextController {

    private final TextToTextService textToText;
    @PostMapping("/generate-text")
    public String generateSpeech(
            @RequestBody String text
    ) {
        return textToText.generateSpeech(text);
    }
}
