package com.example.HealthCare.service;

import com.example.HealthCare.utility.FileUtility;
import com.microsoft.cognitiveservices.speech.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class TextToTextService {

    private static final String GEMINI_API_KEY = "AIzaSyB3bU1IPq0JewT0uJjq3ZANOlg-QvRofWo";
    private static final String MANUALS_PATH = "D:\\OWN INFORMATONS\\GIT PROJECTS\\HealthCareAIApp\\src\\main\\resources\\manuals";
    private static final String PDF_OUTPUT_PATH = "D:\\OWN INFORMATONS\\GIT PROJECTS\\HealthCareAIApp\\src\\main\\resources\\output.pdf";
    private static final String SPEECH_KEY = "4680636dd3d541c4be11a4628e14267e";
    private static final String SPEECH_REGION = "australiaeast";
//    public String generateSpeech(String text) {
//        try {
//            // Escape the text to ensure it doesn't contain any invalid characters
//            text = text.replace("\"", "\\\"");
//
//            // API endpoint URL
//            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY);
//
//            // Open connection
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            // Set request method
//            connection.setRequestMethod("POST");
//
//            // Set request headers
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            // Enable output/input streams
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//
//            // Request body
//            String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + text + "\"}]}]}";
//
//            // Write request body
//            OutputStream outputStream = connection.getOutputStream();
//            outputStream.write(requestBody.getBytes());
//            outputStream.flush();
//            outputStream.close();
//
//            // Get response code
//            int responseCode = connection.getResponseCode();
//            System.out.println("Response Code: " + responseCode);
//
//            // Read response
//            BufferedReader reader;
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            } else {
//                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
//            }
//            String inputLine;
//            StringBuilder response = new StringBuilder();
//            while ((inputLine = reader.readLine()) != null) {
//                response.append(inputLine);
//            }
//            reader.close();
//
//            // Return response
//            return response.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred: " + e.getMessage();
//        }
//    }

    public String generateTextToSpeech(String text) {
        try {
            // Escape the text to ensure it doesn't contain any invalid characters
            text = text.replace("\"", "\\\"");

            // API endpoint URL
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("POST");

            // Set request headers
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable output/input streams
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Request body
            String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + text + "\"}]}]}";

            // Write request body
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.getBytes());
            }

            // Get response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream()))) {
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // Close connection
            connection.disconnect();

            // Return response
            return extractTextFromJsonResponse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    //!!!!!

//    public String generateTextToText(String text) {
//        try {
//            // Escape the text to ensure it doesn't contain any invalid characters
//            text = text.replace("\"", "\\\"");
//
//            // API endpoint URL
//            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY);
//
//            // Open connection
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            // Set request method
//            connection.setRequestMethod("POST");
//
//            // Set request headers
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            // Enable output/input streams
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//
//            // Request body
//            String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + text + "\"}]}]}";
//
//            // Write request body
//            try (OutputStream outputStream = connection.getOutputStream()) {
//                outputStream.write(requestBody.getBytes());
//            }
//
//            // Get response code
//            int responseCode = connection.getResponseCode();
//            System.out.println("Response Code: " + responseCode);
//
//            // Read response
//            StringBuilder response = new StringBuilder();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream()))) {
//                String inputLine;
//                while ((inputLine = reader.readLine()) != null) {
//                    response.append(inputLine);
//                }
//            }
//
//            // Close connection
//            connection.disconnect();
//
//            // Return response
//            return extractTextFromJsonResponse(response.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error occurred: " + e.getMessage();
//        }
//    }

    public String generateTextToText(String text) throws Exception {
        // Save the text input as a PDF file
        FileUtility.saveTextAsPdf(text, PDF_OUTPUT_PATH);

        // Read manuals and other files from the server
        String manualsContent = readManuals();

        // Combine the user input with the manuals content
        String combinedContent = text + "\n\n" + manualsContent;

        // Send the combined content to Gemini API for analysis
        return sendToGemini(combinedContent);
    }


    private String readManuals() throws IOException {
        return Files.walk(Paths.get(MANUALS_PATH))
                .filter(Files::isRegularFile)
                .map(path -> {
                    try {
                        return Files.readString(path, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "";
                    }
                })
                .collect(Collectors.joining("\n\n"));
    }

    private String sendToGemini(String text) throws IOException {
        // Escape the text to ensure it doesn't contain any invalid characters
        text = text.replace("\"", "\\\"");

        // API endpoint URL
        URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY);

        // Open connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method
        connection.setRequestMethod("POST");

        // Set request headers
        connection.setRequestProperty("Content-Type", "application/json");

        // Enable output/input streams
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // Request body
        String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + text + "\"}]}]}";

        // Write request body
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        // Get response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
        }

        // Close connection
        connection.disconnect();

        // Return response
        return extractTextFromJsonResponse(response.toString());
    }

    private String extractTextFromJsonResponse(String jsonResponse) {
        // Extract text from JSON response
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (!jsonObject.has("candidates")) {
                return "Error: No candidates found in the response";
            }

            JSONArray candidates = jsonObject.getJSONArray("candidates");
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            StringBuilder extractedText = new StringBuilder();
            for (int i = 0; i < parts.length(); i++) {
                JSONObject part = parts.getJSONObject(i);
                String text = part.getString("text");
                extractedText.append(text).append("\n");
            }
            return extractedText.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error extracting text from JSON response: " + e.getMessage();
        }
    }






    // Read and Speech word

//    public byte[] generateTextToSpvveech(String text) throws InterruptedException, ExecutionException {
//        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(SPEECH_KEY, SPEECH_REGION)) {
//            speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");
//            try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig)) {
//                SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
//                if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
//                    System.out.println("Speech synthesized successfully.");
//                    return speechSynthesisResult.getAudioData();
//                } else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
//                    SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
//                    System.out.println("Speech synthesis canceled. Reason=" + cancellation.getReason());
//                    if (cancellation.getReason() == CancellationReason.Error) {
//                        System.out.println("Error details: " + cancellation.getErrorDetails());
//                    }
//                }
//            }
//        }
//        throw new RuntimeException("Failed to synthesize speech.");
//    }





//    public String generateTextToSpeeqqch(String text) throws InterruptedException, ExecutionException {
//        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(SPEECH_KEY, SPEECH_REGION)) {
//            speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");
//            try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig)) {
//                SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
//                if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
//                    System.out.println("Speech synthesized successfully.");
//                    return saveAudioToFile(speechSynthesisResult);
//                } else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
//                    SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
//                    System.out.println("Speech synthesis canceled. Reason=" + cancellation.getReason());
//                    if (cancellation.getReason() == CancellationReason.Error) {
//                        System.out.println("Error details: " + cancellation.getErrorDetails());
//                    }
//                }
//            }
//        }
//        throw new RuntimeException("Failed to synthesize speech.");
//    }
//
//    private String saveAudioToFile(SpeechSynthesisResult synthesisResult) {
//        byte[] audioData = synthesisResult.getAudioData();
//        String filePath = "output.wav"; // Change this to your desired audio file location
//        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//            fos.write(audioData);
//            System.out.println("Audio saved to: " + filePath);
//            return filePath;
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to save audio to file: " + e.getMessage());
//        }
//    }
//
//





//    public String generateTextToSpeechNew(String text) {
//        try {
//            // Escape the text to ensure it doesn't contain any invalid characters
//            text = text.replace("\"", "\\\"");
//
//            // API endpoint URL
//            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY);
//
//            // Open connection
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            // Set request method
//            connection.setRequestMethod("POST");
//
//            // Set request headers
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            // Enable output/input streams
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//
//            // Request body
//            String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + text + "\"}]}]}";
//
//            // Write request body
//            try (OutputStream outputStream = connection.getOutputStream()) {
//                outputStream.write(requestBody.getBytes());
//            }
//
//            // Get response code
//            int responseCode = connection.getResponseCode();
//            System.out.println("Response Code: " + responseCode);
//
//            // Read response
//            StringBuilder response = new StringBuilder();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream()))) {
//                String inputLine;
//                while ((inputLine = reader.readLine()) != null) {
//                    response.append(inputLine);
//                }
//            }
//
//            // Close connection
//            connection.disconnect();
//
//            // Extract text from the JSON response
//            String extractedText = extractTextFromJsonResponse(response.toString());
//
//            // Return the extracted text
//            return extractedText;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error occurred: " + e.getMessage();
//        }
//    }
//
//    private String extractTextFromJsonResponse(String jsonResponse) {
//        // Extract text from JSON response
//        try {
//            JSONObject jsonObject = new JSONObject(jsonResponse);
//            JSONObject contents = jsonObject.getJSONArray("contents").getJSONObject(0);
//            JSONObject parts = contents.getJSONArray("parts").getJSONObject(0);
//            return parts.getString("text");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error extracting text from JSON response: " + e.getMessage();
//        }
//    }
//
//


//    public void generateTextToSpeech(String text, String audioFilePath) {
//        try {
//            // Create SpeechConfig using Azure Cognitive Services credentials
//            SpeechConfig speechConfig = SpeechConfig.fromSubscription(SPEECH_KEY, SPEECH_REGION);
//
//            // Create AudioConfig to specify output audio file path
//            AudioConfig audioConfig = AudioConfig.fromWavFileOutput(audioFilePath);
//
//            // Create SpeechSynthesizer with SpeechConfig and AudioConfig
//            try (SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, audioConfig)) {
//                // Synthesize text to speech
//                synthesizer.SpeakTextAsync(text).get();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to generate text-to-speech");
//        }
//    }







//    public String generateTextToSpeech(String text) {
//        try {
//            // API endpoint URL
//            URL url = new  URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY);
//
//            // Open connection
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            // Set request method
//            connection.setRequestMethod("POST");
//
//            // Set request headers
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            // Enable output/input streams
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//
//            // Request body
//            String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + text + "\"}]}]}";
//
//            // Write request body
//            OutputStream outputStream = connection.getOutputStream();
//            outputStream.write(requestBody.getBytes());
//            outputStream.flush();
//            outputStream.close();
//
//            // Get response code
//            int responseCode = connection.getResponseCode();
//            System.out.println("Response Code: " + responseCode);
//
//            // Read response
//            BufferedReader reader;
//            StringBuilder response = new StringBuilder();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            } else {
//                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
//            }
//            String inputLine;
//            while ((inputLine = reader.readLine()) != null) {
//                response.append(inputLine);
//            }
//            reader.close();
//
//            // Return response
//            return response.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred: " + e.getMessage();
//        }
//
//    }
    public void convertTextToSpeech(String text, String audioFilePath) throws InterruptedException, ExecutionException {
        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(SPEECH_KEY, SPEECH_REGION)) {
            speechConfig.setSpeechSynthesisVoiceName("en-AU-NatashaNeural");
            try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig)) {
                SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
                if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    System.out.println("Speech synthesized successfully.");
                    // Save the synthesized audio to a file
                    saveAudioToFile(speechSynthesisResult, audioFilePath);
                } else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
                    SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
                    System.out.println("Speech synthesis canceled. Reason=" + cancellation.getReason());
                    if (cancellation.getReason() == CancellationReason.Error) {
                        System.out.println("Error details: " + cancellation.getErrorDetails());
                    }
                }
            }
        }
    }

    public static void saveAudioToFile(SpeechSynthesisResult synthesisResult, String filePath) {
        byte[] audioData = synthesisResult.getAudioData();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(audioData);
            System.out.println("Audio saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to save audio to file: " + e.getMessage());
        }
    }

    public static void saveTextToFile(String text, String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(text);
            System.out.println("Text saved to: " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to save text to file: " + e.getMessage());
        }
    }
}
