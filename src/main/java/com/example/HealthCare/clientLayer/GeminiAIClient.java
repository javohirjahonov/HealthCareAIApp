package com.example.HealthCare.clientLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class GeminiAIClient {
    private static final String GEMINI_API_KEY = "AIzaSyAPT46CIBG2Xtf37rATqvWOAu4DomL8KU4"; // Replace with your actual API key
    private static final Logger LOGGER = Logger.getLogger(GeminiAIClient.class.getName());
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = TimeUnit.SECONDS.toMillis(10);

    public String generateResponse(String analyzedManuals, String userInput) throws IOException {
        String combinedInput = analyzedManuals + "\nUser Input:\n" + userInput;
        return extractTextFromJsonResponse(callGeminiAPIWithRetries(combinedInput));
    }

    private String callGeminiAPIWithRetries(String text) throws IOException {
        int attempt = 0;
        long backoff = RETRY_DELAY_MS;

        while (attempt < MAX_RETRIES) {
            attempt++;
            try {
                return callGeminiAPI(text).getJsonResponse();
            } catch (IOException e) {
                if (e.getMessage().contains("Response Code: 429")) {
                    LOGGER.log(Level.WARNING, "Rate limit exceeded. Retrying... Attempt " + attempt);
                    try {
                        Thread.sleep(backoff);
                        backoff *= 2; // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted while waiting to retry", ie);
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new IOException("Max retries exceeded");
    }

    private ApiResponse callGeminiAPI(String query) throws IOException {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY;
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = "{\"contents\": [{\"parts\": [{\"text\": \"" + query.replace("\"", "\\\"") + "\"}]}]}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        String jsonResponse;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) ? connection.getInputStream() : connection.getErrorStream(), StandardCharsets.UTF_8))) {
            jsonResponse = br.lines().reduce("", (acc, line) -> acc + line);
        }

        return new ApiResponse(responseCode, jsonResponse);
    }

    private String extractTextFromJsonResponse(String jsonResponse) {
        try {
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Empty JSON response received");
                return "Error: Empty JSON response";
            }

            jsonResponse = jsonResponse.trim();

            if (!jsonResponse.startsWith("{")) {
                LOGGER.log(Level.SEVERE, "Unexpected JSON format received: " + jsonResponse);
                return "Error: Unexpected JSON format";
            }

            JSONObject jsonObject = new JSONObject(jsonResponse);
            LOGGER.log(Level.INFO, "Full JSON Response: " + jsonResponse);

            if (jsonObject.has("error")) {
                JSONObject error = jsonObject.getJSONObject("error");
                return "Error: " + error.getString("message");
            }

            if (!jsonObject.has("candidates")) {
                LOGGER.log(Level.SEVERE, "No candidates found in JSON response: " + jsonResponse);
                return "Error: No candidates found";
            }

            JSONArray candidates = jsonObject.getJSONArray("candidates");
            if (candidates.length() == 0) {
                LOGGER.log(Level.WARNING, "Empty candidates array in JSON response: " + jsonResponse);
                return "Error: Empty candidates array";
            }

            JSONObject candidate = candidates.getJSONObject(0);
            if (!candidate.has("content")) {
                LOGGER.log(Level.SEVERE, "No content found in candidate JSON object: " + candidate);
                return "Error: No content found. JSON Object: " + candidate.toString();
            }

            JSONObject content = candidate.getJSONObject("content");
            if (!content.has("parts")) {
                LOGGER.log(Level.SEVERE, "No parts found in content JSON object: " + content);
                return "Error: No parts found";
            }

            JSONArray parts = content.getJSONArray("parts");
            StringBuilder extractedText = new StringBuilder();
            for (int i = 0; i < parts.length(); i++) {
                JSONObject part = parts.getJSONObject(i);
                if (part.has("text")) {
                    String text = part.getString("text");
                    extractedText.append(text).append("\n");
                } else {
                    LOGGER.log(Level.WARNING, "No 'text' field found in part JSON object: " + part);
                }
            }
            return extractedText.toString();
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Error extracting text from JSON response: " + e.getMessage(), e);
            return "Error extracting text from JSON response: " + e.getMessage();
        }
    }

    private static class ApiResponse {
        private final int responseCode;
        private final String jsonResponse;

        public ApiResponse(int responseCode, String jsonResponse) {
            this.responseCode = responseCode;
            this.jsonResponse = jsonResponse;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getJsonResponse() {
            return jsonResponse;
        }
    }
}
