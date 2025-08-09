package com.example.speech.CTOPP.Subtest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.example.speech.StreamingMicTranscriber;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MemoryForDigitsTest {

    public static void run() {
        List<String> digitSequences = loadData("/Users/aarushagrawal/Documents/dyslexiaApp/speech-to-text/src/main/java/com/example/speech/CTOPP/data/memory-digits.json");
        Scanner scanner = new Scanner(System.in);

        StreamingMicTranscriber transcriber;
        try {
            transcriber = new StreamingMicTranscriber();
        } catch (Exception e) {
            System.out.println("Failed to initialize microphone transcription: " + e.getMessage());
            return;
        }

        int total = 0, correct = 0;

        System.out.println("Memory for Digits Test");

        Collections.shuffle(digitSequences);
        List<String> selected = digitSequences.subList(0, Math.min(10, digitSequences.size()));

        for (String sequence : selected) {
            System.out.printf("\nListen to this sequence of digits and repeat it back:\n%s\n", sequence);
            System.out.println("Type '1' and press Enter to START recording your answer.");
            String startCmd = scanner.nextLine();

            if (startCmd.equals("1")) {
                try {
                    transcriber.startListening();
                    System.out.println("Recording started. Please wait a moment...");
                    Thread.sleep(2000); // stabilize mic before speaking
                } catch (Exception e) {
                    System.out.println("Error starting recording: " + e.getMessage());
                    continue;
                }

                System.out.println("Recording... Type '2' and press Enter to STOP recording.");
                String stopCmd = scanner.nextLine();

                if (stopCmd.equals("2")) {
                    String response = "";
                    try {
                        response = transcriber.stopListeningAndGetTranscript();
                    } catch (Exception e) {
                        System.out.println("Error stopping recording: " + e.getMessage());
                    }

                    System.out.println("You said (transcript): " + response);

                    total++;
                    String normalizedResponse = response.replaceAll("\\s+", "");
                    if (normalizedResponse.equals(sequence)) {
                        correct++;
                    }
                }
            }
        }

        System.out.printf("\nYour Score: %d out of %d correct\n", correct, total);
    }

    private static List<String> loadData(String path) {
        List<String> sequences = new ArrayList<>();
        try {
            JsonArray arr = JsonParser.parseReader(new FileReader(path)).getAsJsonArray();
            for (JsonElement e : arr) {
                sequences.add(e.getAsString());
            }
        } catch (Exception e) {
            System.out.println("Failed to load Memory for Digits JSON: " + e.getMessage());
        }
        return sequences;
    }
}
