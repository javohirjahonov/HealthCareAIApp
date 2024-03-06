package com.example.demo;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class TextToText {
    private static final String GEMINI_API_KEY = "AIzaSyBUPcSMKyKk5B0DKCdznjiiyJQao2I1RTc";

    public static void main(String[] args) {
        String userText = "Enter your text here";

        try {
            String generatedText = generateText(userText);
            System.out.println("Generated Text: " + generatedText);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public static String generateText(String text) throws IOException {
        // Prepare the request payload
        JSONObject payload = new JSONObject();
        payload.put("text", text);

        // Create the request body
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());

        // Build the request
        Request request = new Request.Builder()
                .url("https://api.gemini.com/v1beta/generate-text")
                .post(body)
                .addHeader("Authorization", "Bearer " + GEMINI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // Send the request and parse the response
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }

            // Parse the response JSON
            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse.getString("generated_text");
        }
    }
}
