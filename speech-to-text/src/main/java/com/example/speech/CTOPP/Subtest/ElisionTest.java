package com.example.speech.CTOPP.Subtest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.example.speech.StreamingMicTranscriber;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// example of Elision: say “cowboy” without “cow”

public class ElisionTest {

    public static void run() {
        Map<String, List<String>> wordMap = loadData("/Users/aarushagrawal/Documents/dyslexiaApp/speech-to-text/src/main/java/com/example/speech/CTOPP/data/elision.json");
        Scanner scanner = new Scanner(System.in);

        StreamingMicTranscriber transcriber;
        try {
            transcriber = new StreamingMicTranscriber();
        } catch (Exception e) {
            System.out.println("Failed to initialize microphone transcription: " + e.getMessage());
            return;
        }

        int total = 0, correct = 0;

        System.out.println("\nElision Test");

        List<String> keys = new ArrayList<>(wordMap.keySet());
        Collections.shuffle(keys);
        keys = keys.subList(0, Math.min(10, keys.size())); // Limit to 10 questions

        for (String word : keys) {
            List<String> syllables = wordMap.get(word);

            boolean removeFirst = new Random().nextBoolean();
            String removed = removeFirst ? syllables.get(0) : syllables.get(syllables.size() - 1);
            List<String> remaining = removeFirst
                    ? syllables.subList(1, syllables.size())
                    : syllables.subList(0, syllables.size() - 1);
            String expected = String.join("", remaining);

            System.out.printf("\nSay the word '%s' without '%s':\n", word, removed);
            System.out.println("Type '1' and press Enter to START recording your answer.");
            String startCmd = scanner.nextLine();

            if (startCmd.equals("1")) {
                try {
                    transcriber.startListening();
                    System.out.println("Recording started. Please wait a moment...");
                    Thread.sleep(2000); // allow mic to stabilize
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
                    // normalize response and expected to ignore spaces/case
                    String normResp = response.replaceAll("\\s+", "").toLowerCase();
                    String normExp = expected.toLowerCase();

                    if (normResp.equals(normExp) || normResp.equals(normExp + "ed")) {
                        correct++;
                    }
                }
            }
        }

        System.out.printf("\nYour Score: %d out of %d correct\n", correct, total);

        scanner.close();
    }

    private static Map<String, List<String>> loadData(String path) {
        Map<String, List<String>> wordMap = new LinkedHashMap<>();
        try {
            JsonObject obj = JsonParser.parseReader(new FileReader(path)).getAsJsonObject();
            for (String key : obj.keySet()) {
                JsonArray arr = obj.getAsJsonArray(key);
                List<String> syllables = new ArrayList<>();
                for (JsonElement e : arr) {
                    syllables.add(e.getAsString());
                }
                wordMap.put(key, syllables);
            }
        } catch (Exception e) {
            System.out.println("Failed to load Elision JSON: " + e.getMessage());
        }
        return wordMap;
    }
}
