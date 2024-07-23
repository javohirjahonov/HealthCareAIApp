package com.example.HealthCare.rag;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RAGExample {
    private static GeminiAIClient geminiAIClient = new GeminiAIClient();
    private static DocumentRetriever documentRetriever = new DocumentRetriever();
    private static final Logger LOGGER = Logger.getLogger(RAGExample.class.getName());

    public static void main(String[] args) {
        try {
            // Path to the medical report file
            String medicalReportPath = "D:\\serverbase\\medicalreport.pdf";
            // Path to the manual PDF file
            String pdfFilePath = "D:\\serverbase\\medicalreport.pdf";
            String query = "analyze the following laboratory analysis";

            // Read the medical report file content
            String medicalReport = FileUtils.readFileContent(medicalReportPath);

            // Retrieve relevant documents based on the query
            String[] retrievedDocs = documentRetriever.retrieveDocuments(pdfFilePath, query);

            // Combine the retrieved documents into a single string
            StringBuilder retrievedText = new StringBuilder();
            for (String doc : retrievedDocs) {
                retrievedText.append(doc).append("\n");
            }

            // Generate a response by combining the analyzed manuals and the medical report
            String response = geminiAIClient.generateResponse(retrievedText.toString(), medicalReport);

            // Output the generated response
            System.out.println("Generated Response: \n" + response);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred: " + e.getMessage(), e);
        }
    }
}
