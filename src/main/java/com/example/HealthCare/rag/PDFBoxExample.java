package com.example.HealthCare.rag;

import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.File;
import java.io.IOException;

public class PDFBoxExample {
    public static void main(String[] args) {
        PDDocument document = null;
        try {
            File file = new File("D:\\serverbase\\medicine.pdf");
            document = PDDocument.load(file);
            // Perform operations on the document
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

