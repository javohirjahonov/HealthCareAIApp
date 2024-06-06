package com.example.demo;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class TextToSpeechExample {
    public static void main(String[] args) {
        String googleApplicationCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        System.out.println("GOOGLE_APPLICATION_CREDENTIALS: " + googleApplicationCredentials);

        if (googleApplicationCredentials == null || googleApplicationCredentials.isEmpty()) {
            System.err.println("Environment variable GOOGLE_APPLICATION_CREDENTIALS is not set.");
            return;
        }

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText("hello").build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("es-US").setName("es-US-Studio-B").build();
            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).setSpeakingRate(1.0).build();
            com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();
            try (OutputStream out = new FileOutputStream("output.wav")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.wav\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
