package com.example.speech.CTOPP.Subtest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class BlendingTest {
    //example of blending words test - Blending words: user hears “m/oo/se” and must say moose

    public static void run() {
        List<List<String>> wordPartsList = loadData("data/blending-words.json");
        Scanner scanner = new Scanner(System.in);
        int total = 0, correct = 0;

        System.out.println("\nBlending Words Test");

        Collections.shuffle(wordPartsList);
        List<List<String>> selected = wordPartsList.subList(0, Math.min(10, wordPartsList.size()));

        for (List<String> parts : selected) {
            String display = String.join(" - ", parts); //adds dashes between each syllable
            String expected = String.join("", parts); //joins the syllables together

            System.out.printf("Blend these sounds together to make a word: %s\n", display);
            System.out.print("Your answer: ");
            String response = scanner.nextLine().trim().toLowerCase();

            total++;
            if (response.equals(expected)) {
                correct++;
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
