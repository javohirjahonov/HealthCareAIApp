package com.example.HealthCare.controller.image;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageToSpeechController {

    @PostMapping("/test")
    public String signIn(){
        return "Successfully Connected to Azure Server !!! ";
    }
}
