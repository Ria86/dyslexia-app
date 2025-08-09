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

public class RapidSymbolicNamingTest {

    public static void run() {
        List<List<String>> itemsList = loadData("/Users/aarushagrawal/Documents/dyslexiaApp/speech-to-text/src/main/java/com/example/speech/CTOPP/data/rapid-symbolic.json");
        Scanner scanner = new Scanner(System.in);

        StreamingMicTranscriber transcriber;
        try {
            transcriber = new StreamingMicTranscriber();
        } catch (Exception e) {
            System.out.println("Failed to initialize microphone transcription: " + e.getMessage());
            return;
        }

        int totalItems = 0;
        int correctCount = 0;
        long totalTime = 0;

        System.out.println("\nRapid Symbolic Naming Test");
        System.out.println("You will see a list of symbols (letters/numbers). Name all aloud as quickly as possible.");
        System.out.println("Use voice recording: type '1' and press Enter to START recording, '2' and press Enter to STOP.");

        Collections.shuffle(itemsList);
        List<List<String>> selected = itemsList.subList(0, Math.min(3, itemsList.size()));

        for (List<String> items : selected) {
            System.out.println("\nSymbols to name: " + String.join(" ", items));
            System.out.println("Type '1' and press Enter to START recording your answer.");
            String startCmd = scanner.nextLine();

            if (startCmd.equals("1")) {
                try {
                    transcriber.startListening();
                    System.out.println("Recording started. Please wait a moment...");
                    Thread.sleep(2000); // short delay before recording your answer
                } catch (Exception e) {
                    System.out.println("Error starting recording: " + e.getMessage());
                    continue;
                }

                System.out.println("Recording... Type '2' and press Enter to STOP recording.");
                long startTime = System.currentTimeMillis();

                String stopCmd = scanner.nextLine();

                if (stopCmd.equals("2")) {
                    String response = "";
                    try {
                        response = transcriber.stopListeningAndGetTranscript();
                    } catch (Exception e) {
                        System.out.println("Error stopping recording: " + e.getMessage());
                    }

                    long endTime = System.currentTimeMillis();
                    long timeTaken = endTime - startTime;
                    totalTime += timeTaken;

                    System.out.println("You said (transcript): " + response);

                    String[] spokenItems = response.trim().toLowerCase().split("\\s+");
                    int correctThisRound = 0;

                    for (int i = 0; i < Math.min(items.size(), spokenItems.length); i++) {
                        if (spokenItems[i].equalsIgnoreCase(items.get(i))) {
                            correctThisRound++;
                        }
                    }

                    totalItems += items.size();
                    correctCount += correctThisRound;

                    System.out.printf("Time taken: %.2f seconds\n", timeTaken / 1000.0);
                    System.out.printf("Correctly named: %d out of %d\n", correctThisRound, items.size());
                }
            }
        }

        System.out.printf("\nOverall score: %d/%d correct\n", correctCount, totalItems);
        System.out.printf("Total time spent: %.2f seconds\n", totalTime / 1000.0);
    }

    private static List<List<String>> loadData(String path) {
        List<List<String>> listOfItems = new ArrayList<>();
        try {
            JsonArray arr = JsonParser.parseReader(new FileReader(path)).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonArray itemsArr = e.getAsJsonArray();
                List<String> items = new ArrayList<>();
                for (JsonElement item : itemsArr) {
                    items.add(item.getAsString());
                }
                listOfItems.add(items);
            }
        } catch (Exception e) {
            System.out.println("Failed to load Rapid Symbolic Naming JSON: " + e.getMessage());
        }
        return listOfItems;
    }
}
