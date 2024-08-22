package com.example.rag;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentRetriever {
    private static final Logger LOGGER = Logger.getLogger(DocumentRetriever.class.getName());
    private static final int MAX_SECTIONS = 3;

    public String[] retrieveDocuments(String pdfFilePath, String query) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(document.getNumberOfPages());
            String pdfContent = pdfStripper.getText(document);

            // Split the content into sections
            String[] documentSections = pdfContent.split("\\r?\\n\\r?\\n");

            // Find sections that contain the query
            List<String> matchingSections = new ArrayList<>();
            for (String section : documentSections) {
                if (section.contains(query)) {
                    matchingSections.add(section);
                    if (matchingSections.size() >= MAX_SECTIONS) {
                        break;
                    }
                }
            }

            // Return matching sections or a message if none found
            if (matchingSections.isEmpty()) {
                return new String[]{"No matching sections found for the query: " + query};
            }
            return matchingSections.toArray(new String[0]);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading PDF document: " + e.getMessage(), e);
            throw e;
        }
    }
}
