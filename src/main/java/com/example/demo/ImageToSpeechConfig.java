package com.example.demo;

import com.microsoft.cognitiveservices.speech.*;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ImageToSpeechConfig {
    private static String speechKey = "4ecc50ebd0804d48b896989838db8972";
    private static String speechRegion = "eastus";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);

        speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");

        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);

        System.out.println("Enter some text that you want to speak >");
        String text = new Scanner(System.in).nextLine();
        if (text.isEmpty()) {
            return;
        }

        SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();

        if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
            System.out.println("Speech synthesized to speaker for text [" + text + "]");
        } else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
            System.out.println("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                System.out.println("CANCELED: Did you set the speech resource key and region values?");
            }
        }

        System.exit(0);
    }
}