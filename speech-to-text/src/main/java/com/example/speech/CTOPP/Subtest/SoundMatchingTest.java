package com.example.speech.CTOPP.Subtest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

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
        List<Question> questions = loadData("/CTOPP/data/sound-matching.json");
        Scanner scanner = new Scanner(System.in);
        int total = 0, correct = 0;

        System.out.println("\nSound Matching Test");

        Collections.shuffle(questions);
        for (Question q : questions.subList(0, Math.min(10, questions.size()))) {
            System.out.printf("\nSelect the word that begins with the same sound as '%s':\n", q.targetWord);

            for (int i = 0; i < q.options.size(); i++) {
                System.out.printf("%d: %s\n", i + 1, q.options.get(i).word);
            }

            System.out.print("Your choice (1-3): ");
            String input = scanner.nextLine().trim();
            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }

            total++;
            if (choice == 1) {  // First option is always correct
                correct++;
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
