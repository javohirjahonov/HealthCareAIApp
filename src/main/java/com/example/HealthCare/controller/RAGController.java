package com.example.HealthCare.controller;

import com.example.HealthCare.service.RAGService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RAGController {

    private static final Logger LOGGER = Logger.getLogger(RAGController.class.getName());
    private final RAGService ragService;

    private static final String DEFAULT_MANUAL_PATH = "D:\\PDP UNIVERSITY\\HealthCareAIApp\\src\\main\\resources\\medicine.pdf";
    private static final String DEFAULT_QUERY = "If there is any information in the medical analysis that would justify a visit to the doctor or a medical appointment, say so with a bulleted list of reasons why and if appropriate, topics or suggested questions for the doctor. At the end, also state that this recommendation is generated by AI and that it may need confirmation by a medical professional.";

    @Operation(summary = "Generate a response based on the uploaded medical report")
    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateResponse(@RequestParam("file") MultipartFile medicalReport) {
        if (medicalReport.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        try {
            // Save the uploaded file to a temporary location
            String medicalReportPath = saveUploadedFile(medicalReport);

            // Retry logic to check file existence after saving
            boolean fileExists = false;
            for (int i = 0; i < 5; i++) {
                if (new File(medicalReportPath).exists()) {
                    fileExists = true;
                    break;
                }
                LOGGER.warning("File not found after saving, retrying... Attempt: " + (i + 1));
                TimeUnit.MILLISECONDS.sleep(500); // Wait 500ms before retrying
            }

            if (!fileExists) {
                LOGGER.warning("File still not found after retries: " + medicalReportPath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: File not found after saving: " + medicalReportPath);
            }

            // Proceed with generating the response
            String response = ragService.generateResponse(DEFAULT_MANUAL_PATH, medicalReportPath, DEFAULT_QUERY);
            return ResponseEntity.ok().body(response);
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while generating response: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    private String saveUploadedFile(MultipartFile file) throws IOException {
        String customDir = "C:\\Users\\user\\Desktop\\imgs\\";
        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(customDir, uniqueFileName);

        Files.createDirectories(filePath.getParent());

        Files.copy(file.getInputStream(), filePath);

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            LOGGER.info("File saved successfully: " + filePath.toString());
        } else {
            LOGGER.warning("File not saved or not found: " + filePath.toString());
        }

        return filePath.toString();
    }

}
