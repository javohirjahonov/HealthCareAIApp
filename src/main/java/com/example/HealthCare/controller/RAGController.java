package com.example.HealthCare.controller;

import com.example.HealthCare.service.RAGService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RAGController {

    private static final Logger LOGGER = Logger.getLogger(RAGController.class.getName());
    private final RAGService ragService;

    private static final String DEFAULT_MANUAL_PATH = "D:\\PDP UNIVERSITY\\HealthCareAIApp\\src\\main\\resources\\medicine.pdf"; // Set your default manual path
    private static final String DEFAULT_QUERY = "Analyze the medical analysis report and provide accurate information only in text form, numbers are not needed, calculations are needed, only a summary is needed"; // Set your default query text

//    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Generate a response based on the uploaded medical report")
    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateResponse(@RequestParam("file") MultipartFile medicalReport) {
        if (medicalReport.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        try {
            // Save the uploaded file to a temporary location
            String medicalReportPath = saveUploadedFile(medicalReport);
            String response = ragService.generateResponse(DEFAULT_MANUAL_PATH, medicalReportPath, DEFAULT_QUERY);
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while generating response: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String filePath = tempDir + "/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }
}
