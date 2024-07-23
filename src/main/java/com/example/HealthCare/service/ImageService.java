package com.example.HealthCare.service;

import com.example.HealthCare.domain.entity.image.ImageDataType;
import com.example.HealthCare.domain.entity.image.ImageEntity;
import com.example.HealthCare.repository.ImageDataRepository;
import com.microsoft.cognitiveservices.speech.*;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageDataRepository imageRepository;
    private static final String SPEECH_KEY = "4680636dd3d541c4be11a4628e14267e";
    private static final String SPEECH_REGION = "australiaeast";
    private static final String GOOGLE_API_KEY = "AIzaSyCMm0oV_eZCVxswv-r244_S5BSVHEsMkPc";
//    public String processImage(MultipartFile file) {
//        try {
//            byte[] imageData = file.getBytes();
//            // Save image to database
//            ImageEntity image = new ImageEntity();
//            image.setImageData(imageData);
//            imageRepository.save(image);
//            return "Image uploaded successfully";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Failed to upload image";
//        }
//    }
    public String generateImageToText(MultipartFile file, String prompt) throws IOException {
        ImageEntity image = ImageEntity.builder()
                .imageData(file.getBytes())
                .dataType(ImageDataType.IMAGE_TO_TEXT)
                .build();
        ImageEntity imageEntity = imageRepository.save(image);

        try {
            byte[] imageBytes = imageEntity.getImageData();
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

    public String generateImageToSpeech(MultipartFile file, String prompt) throws IOException {
        ImageEntity image = ImageEntity.builder()
                .imageData(file.getBytes())
                .dataType(ImageDataType.IMAGE_TO_SPEECH)
                .build();
        ImageEntity imageEntity = imageRepository.save(image);

        try {
            byte[] imageBytes = imageEntity.getImageData();
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

//    public void generateImageDescription(MultipartFile file, String prompt) throws IOException {
//        try {
//            byte[] imageBytes = file.getBytes();
//            String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
//            Map<String, Object> inlineData = new HashMap<>();
//            inlineData.put("mime_type", "image/jpeg");
//            inlineData.put("data", imageBase64);
//            Map<String, Object> partImage = new HashMap<>();
//            partImage.put("inline_data", inlineData);
//
//            Map<String, Object> partText = new HashMap<>();
//            partText.put("text", prompt);
//
//            Map<String, Object> contentPart = new HashMap<>();
//            contentPart.put("parts", new Object[]{partText, partImage});
//
//            Map<String, Object>[] contents = new Map[]{contentPart};
//
//            Map<String, Object> payloadMap = new HashMap<>();
//            payloadMap.put("contents", contents);
//
//            JSONObject payload = new JSONObject(payloadMap);
//
//            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro-vision:generateContent?key=" + GOOGLE_API_KEY;
//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            OkHttpClient client = new OkHttpClient();
//            RequestBody body = RequestBody.create(JSON, payload.toString());
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(body)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            try (Response response = client.newCall(request).execute()) {
//                String responseString = response.body().string();
//                JSONObject responseObject = new JSONObject(responseString);
//                JSONObject content = responseObject.getJSONArray("candidates").getJSONObject(0).getJSONObject("content");
//                StringBuilder generatedText = new StringBuilder();
//                for (int i = 0; i < content.getJSONArray("parts").length(); i++) {
//                    String text = content.getJSONArray("parts").getJSONObject(i).optString("text", "");
//                    generatedText.append(text).append(" ");
//                }
//                return generatedText.toString().trim();
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return "An error occurred while generating the description.";
//        }
//    }

    public static void convertTextToSpeech(String text, String basePath) throws InterruptedException, ExecutionException {
        try (SpeechConfig speechConfig = SpeechConfig.fromSubscription(SPEECH_KEY, SPEECH_REGION)) {
            speechConfig.setSpeechSynthesisVoiceName("en-GB-LibbyNeural");
            try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig)) {
                SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
                if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    System.out.println("Speech synthesized successfully.");
                    // Save the synthesized audio to a file with voice owner, timestamp, and description
                    saveAudioToFile(speechSynthesisResult, basePath);
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


    public static void saveAudioToFile(SpeechSynthesisResult synthesisResult, String basePath) {
        byte[] audioData = synthesisResult.getAudioData();
        // Generate the current timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // Create the file path using basePath, voice owner, timestamp, and description
        String filePath = basePath + "\\" + "_" + timestamp + "_"  + ".wav";
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
