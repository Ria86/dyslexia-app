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

public class BlendingTest {

    public static void run() {
        List<List<String>> wordPartsList = loadData("/Users/aarushagrawal/Documents/dyslexiaApp/speech-to-text/src/main/java/com/example/speech/CTOPP/data/blending-words.json");
        Scanner scanner = new Scanner(System.in);

        StreamingMicTranscriber transcriber;
        try {
            transcriber = new StreamingMicTranscriber();
        } catch (Exception e) {
            System.out.println("Failed to initialize microphone transcription: " + e.getMessage());
            return;
        }

        int total = 0, correct = 0;

        System.out.println("\nBlending Words Test");

        Collections.shuffle(wordPartsList);
        List<List<String>> selected = wordPartsList.subList(0, Math.min(4, wordPartsList.size()));

        for (List<String> parts : selected) {
            String display = String.join(" - ", parts); // adds dashes between each syllable
            String expected = String.join("", parts);   // joins the syllables together

            System.out.println();
            System.out.println();
            System.out.println("Type '1' and press Enter to START recording your answer.");
            String startCmd = scanner.nextLine();
            if (startCmd.equals("1")) {
                try {
                    transcriber.startListening();
                    Thread.sleep(2000);
                    System.out.printf("Blend these sounds together to make a word: %s\n", display);

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

                    System.out.println();
                    System.out.println("You said (transcript): " + response);

                    total++;
                    if (response.replaceAll("\\s+", "").equalsIgnoreCase(expected)) {
                        correct++;
                    }
            
            }
            
            }
        }

        System.out.printf("\nYour Score: %d out of %d correct\n", correct, total);
    }

    private static List<List<String>> loadData(String path) {
        List<List<String>> wordPartsList = new ArrayList<>();
        try {
            JsonArray arr = JsonParser.parseReader(new FileReader(path)).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonArray parts = e.getAsJsonArray();
                List<String> partList = new ArrayList<>();
                for (JsonElement p : parts) {
                    partList.add(p.getAsString());
                }
                wordPartsList.add(partList);
            }
        } catch (Exception e) {
            System.out.println("Failed to load Blending Words JSON: " + e.getMessage());
        }
        return wordPartsList;
    }
}
