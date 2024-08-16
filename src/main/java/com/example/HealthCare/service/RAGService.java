package com.example.HealthCare.service;

import com.example.HealthCare.clientLayer.GeminiAIClient;
import com.example.HealthCare.utility.FileUtils;
import com.example.HealthCare.repository.DocumentRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RAGService {

    @Autowired
    private DocumentRetriever documentRetriever;

    @Autowired
    private GeminiAIClient geminiAIClient;

    public String generateResponse(String manualPath, String medicalReportPath, String query) throws IOException {
        String medicalReport = FileUtils.readFileContent(medicalReportPath);
        String[] retrievedDocs = documentRetriever.retrieveDocuments(manualPath, query);

        StringBuilder retrievedText = new StringBuilder();
        for (String doc : retrievedDocs) {
            retrievedText.append(doc).append("\n");
        }

        return geminiAIClient.generateResponse(retrievedText.toString(), medicalReport);
    }
}
