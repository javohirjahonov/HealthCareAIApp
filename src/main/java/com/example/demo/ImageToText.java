package com.example.demo;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ImageToText {

    private static final String GOOGLE_API_KEY = "AIzaSyBUPcSMKyKk5B0DKCdznjiiyJQao2I1RTc";

    public static void main(String[] args) throws IOException {
        String imagePath = "C:\\FRONT\\Git Projects\\HealthCareProjectAI\\src\\main\\resources\\images\\img_2.png";
        String prompt = "Analyze the file and provide medical diagnosis information";
//        Analyze the file and prov.3ide medical diagnosis information and information should be 200 words
        try {
            System.out.println(generateImageDescription(imagePath, prompt));
        } catch (Exception e) {
            System.out.println("An error occurred while generating the description.");
            e.printStackTrace();
        }
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
            System.out.println(e.getMessage());
            return "An error occurred while generating the description.";
        }
    }
}