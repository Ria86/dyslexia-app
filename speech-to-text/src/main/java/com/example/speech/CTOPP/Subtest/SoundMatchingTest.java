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

public class SoundMatchingTest {
    static class Question {
        String targetWord;
        List<Option> options;

        static class Option {
            String word;
            String imageSource;

            Option(String w, String img) {
                word = w;
                imageSource = img;
            }
        }
    }

    public static void run() {
        List<Question> questions = loadData("/Users/aarushagrawal/Documents/dyslexiaApp/speech-to-text/src/main/java/com/example/speech/CTOPP/data/sound-matching.json");
        Scanner scanner = new Scanner(System.in);

        StreamingMicTranscriber transcriber;
        try {
            transcriber = new StreamingMicTranscriber();
        } catch (Exception e) {
            System.out.println("Failed to initialize microphone transcription: " + e.getMessage());
            return;
        }

        int total = 0, correct = 0;

        System.out.println("\nSound Matching Test");

        Collections.shuffle(questions);
        for (Question q : questions.subList(0, Math.min(3, questions.size()))) {
            System.out.printf("\nSelect the word that begins with the same sound as '%s':\n", q.targetWord);

            for (int i = 0; i < q.options.size(); i++) {
                System.out.printf("%d: %s\n", i + 1, q.options.get(i).word);
            }

            System.out.println("Type '1' and press Enter to START recording your answer.");
            String startCmd = scanner.nextLine();

            if ("1".equals(startCmd)) {
                try {
                    transcriber.startListening();
                    System.out.println("Recording started. Please wait a moment...");
                    Thread.sleep(2000);  // pause before recording your answer
                } catch (Exception e) {
                    System.out.println("Error starting recording: " + e.getMessage());
                    continue;
                }

                System.out.println("Recording... Type '2' and press Enter to STOP recording.");
                String stopCmd = scanner.nextLine();

                if ("2".equals(stopCmd)) {
                    String transcript = "";
                    try {
                        transcript = transcriber.stopListeningAndGetTranscript().trim().toLowerCase();
                    } catch (Exception e) {
                        System.out.println("Error stopping recording: " + e.getMessage());
                    }

                    System.out.println("You said: " + transcript);

                    // Check if transcript matches the correct answer (option 1 word)
                    total++;
                    if (transcript.contains(q.options.get(0).word.toLowerCase())) {
                        correct++;
                        System.out.println("Correct!");
                    } else {
                        System.out.println("Incorrect. Correct answer was: " + q.options.get(0).word);
                    }
                }
            }
        }

        System.out.printf("\nYour Score: %d out of %d correct\n", correct, total);
    }

    private static List<Question> loadData(String path) {
        List<Question> questions = new ArrayList<>();
        try {
            JsonArray arr = JsonParser.parseReader(new FileReader(path)).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonObject obj = e.getAsJsonObject();
                Question q = new Question();
                q.targetWord = obj.get("target").getAsString();

                JsonArray opts = obj.getAsJsonArray("options");
                q.options = new ArrayList<>();
                for (JsonElement optElem : opts) {
                    JsonObject optObj = optElem.getAsJsonObject();
                    String word = optObj.get("word").getAsString();
                    String img = optObj.has("image") ? optObj.get("image").getAsString() : "";
                    q.options.add(new Question.Option(word, img));
                }
                questions.add(q);
            }
        } catch (Exception e) {
            System.out.println("Failed to load Sound Matching JSON: " + e.getMessage());
        }
        return questions;
    }
}
