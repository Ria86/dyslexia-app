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

public class PhonemeIsolationTest {

    public static void run() {
        List<Question> questions = loadData("/CTOPP/data/phoneme-isolation.json");
        Scanner scanner = new Scanner(System.in);
        int total = 0, correct = 0;

        System.out.println("\nPhoneme Isolation Test");
        Collections.shuffle(questions); //randomizes the order of the questions

        for (Question q : questions.subList(0, Math.min(10, questions.size()))) { //prints first 10 questions
            String word = q.word;
            int index = q.position - 1; //finds the index of the target phoneme
            if (index >= q.phonemes.size()) continue;
            String expected = q.phonemes.get(index);

            System.out.printf("Say sound number %d in the word '%s':\n", q.position, word);

            System.out.print("Your answer: ");
            String response = scanner.nextLine().trim().toLowerCase();

            total++;
            if (response.equals(expected)) {
                correct++;
            }
        }

        System.out.printf("Score: %d/%d correct\n", correct, total);
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
