package com.example.speech.CTOPP.Subtest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.example.speech.StreamingMicTranscriber;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PhonemeIsolationTest {

    public static void run() {
        List<Question> questions = loadData("/Users/aarushagrawal/Documents/dyslexiaApp/speech-to-text/src/main/java/com/example/speech/CTOPP/data/phoneme-isolation.json");
        Scanner scanner = new Scanner(System.in);

        StreamingMicTranscriber transcriber;
        try {
            transcriber = new StreamingMicTranscriber();
        } catch (Exception e) {
            System.out.println("Failed to initialize microphone transcription: " + e.getMessage());
            return;
        }

        int total = 0, correct = 0;

        System.out.println("\nPhoneme Isolation Test");
        Collections.shuffle(questions);

        for (Question q : questions.subList(0, Math.min(10, questions.size()))) {
            String word = q.word;
            int index = q.position - 1;
            if (index >= q.phonemes.size()) continue;
            String expected = q.phonemes.get(index);

            System.out.printf("\nSay sound number %d in the word '%s':\n", q.position, word);
            System.out.println("Type '1' and press Enter to START recording your answer.");
            String startCmd = scanner.nextLine();

            if (startCmd.equals("1")) {
                try {
                    transcriber.startListening();
                    System.out.println("Recording started. Please wait a moment...");
                    Thread.sleep(2000);  // brief pause to stabilize mic
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
                    if (response.trim().toLowerCase().equals(expected.toLowerCase())) {
                        correct++;
                    }
                }
            }
        }

        System.out.printf("\nScore: %d/%d correct\n", correct, total);
    }

    private static List<Question> loadData(String path) {
        List<Question> list = new ArrayList<>();
        try {
            JsonArray arr = JsonParser.parseReader(new FileReader(path)).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonObject obj = e.getAsJsonObject();
                String word = obj.get("word").getAsString();
                int position = obj.get("position").getAsInt();
                List<String> phonemes = new ArrayList<>();
                for (JsonElement ph : obj.get("phonemes").getAsJsonArray()) {
                    phonemes.add(ph.getAsString());
                }
                list.add(new Question(word, position, phonemes));
            }
        } catch (Exception e) {
            System.out.println("Failed to load phoneme isolation data: " + e.getMessage());
        }
        return list;
    }

    private static class Question {
        String word;
        int position;
        List<String> phonemes;

        public Question(String word, int position, List<String> phonemes) {
            this.word = word;
            this.position = position;
            this.phonemes = phonemes;
        }
    }
}
