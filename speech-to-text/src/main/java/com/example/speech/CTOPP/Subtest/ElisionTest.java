package com.example.speech.CTOPP.Subtest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//example of Elision: say “cowboy” without “cow”

public class ElisionTest {

    public static void run() {
        Map<String, List<String>> wordMap = loadData("/CTOPP/data/elision.json"); //list of questions
        Scanner scanner = new Scanner(System.in);
        int total = 0, correct = 0;

        System.out.println("\nElision Test");

        List<String> keys = new ArrayList<>(wordMap.keySet());
        Collections.shuffle(keys);
        keys = keys.subList(0, Math.min(10, keys.size())); // Limit to 10 questions

        for (String word : keys) {
            List<String> syllables = wordMap.get(word);

            boolean removeFirst = new Random().nextBoolean(); //randomly chooses which syllable to remove
            String removed = removeFirst ? syllables.get(0) : syllables.get(syllables.size() - 1); //stores removed syllable
            List<String> remaining = removeFirst //new list for the reamining syllables
                    ? syllables.subList(1, syllables.size())
                    : syllables.subList(0, syllables.size() - 1);
            String expected = String.join("", remaining); //joins the list of the remaining syllables

            System.out.printf("Say the word '%s' without '%s':\n", word, removed);
            System.out.print("Your answer: ");
            String response = scanner.nextLine().trim().toLowerCase();

            total++;
            if (response.equals(expected) || response.equals(expected + "ed")) {
                correct++;
            }
        }

        System.out.printf("\nYour Score: %d out of %d correct\n", correct, total); // for debugging; prints out how many questions the user got correct
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
