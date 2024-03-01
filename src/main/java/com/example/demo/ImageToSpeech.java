package com.example.demo;

import com.microsoft.cognitiveservices.speech.*;
import okhttp3.*;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ImageToSpeech {
    private static final String SPEECH_KEY = "4ecc50ebd0804d48b896989838db8972";
    private static final String SPEECH_REGION = "eastus";
    private static final String GOOGLE_API_KEY = "AIzaSyBUPcSMKyKk5B0DKCdznjiiyJQao2I1RTc";

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // Analyze the image and generate medical diagnosis information
        String imagePath = "C:\\FRONT\\Git Projects\\demo\\src\\main\\resources\\images\\img_1.png";
        String prompt = "Please analyze the medical image provided and generate a succinct diagnosis report. Include the disease name, current status, recommended treatment (including crackle treatment if applicable), and any other pertinent diagnostic information. Accuracy and clarity are paramount for effective patient management. Thank you.";
        String generatedText = generateImageDescription(imagePath, prompt);

        // Convert the generated text to speech
        String audioFilePath = "output.wav";
        convertTextToSpeech(generatedText, audioFilePath);

//        convertTextToSpeech(generatedText, audioFilePath);

        // Save the generated text to a file
        String textFilePath = "output.txt";
        saveTextToFile(generatedText, textFilePath);
    }

    public static String generateImageDescription(String imagePath, String prompt) throws IOException {
        try {
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
            String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", "image/jpeg");
            inlineData.put("data", imageBase64);

            Map<String, Object> partImage = new HashMap<>();
            partImage.put("inline_data", inlineData);

            Map<String, Object> partText = new HashMap<>();
            partText.put("text", prompt);

            Map<String, Object> contentPart = new HashMap<>();
            contentPart.put("parts", new Object[]{partText, partImage});

            Map<String, Object>[] contents = new Map[]{contentPart};

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("contents", contents);

            // Send request to Google Vision API
            // (You need to replace YOUR_GOOGLE_API_KEY with your actual API key)
            JSONObject payload = new JSONObject(payloadMap);
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro-vision:generateContent?key=" + GOOGLE_API_KEY;
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, payload.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseString = response.body().string();
                JSONObject responseObject = new JSONObject(responseString);
                JSONObject content = responseObject.getJSONArray("candidates").getJSONObject(0).getJSONObject("content");
                StringBuilder generatedText = new StringBuilder();
                for (int i = 0; i < content.getJSONArray("parts").length(); i++) {
                    String text = content.getJSONArray("parts").getJSONObject(i).optString("text", "");
                    generatedText.append(text).append(" ");
                }
                return generatedText.toString().trim();
            }
        } catch (Exception e) {
            System.out.println("An error occurred while generating the description.");
            e.printStackTrace();
            return "An error occurred while generating the description.";
        }
    }

    public static void convertTextToSpeech(String text, String audioFilePath) throws InterruptedException, ExecutionException {
        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(SPEECH_KEY, SPEECH_REGION)) {
            speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");
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
