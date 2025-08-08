package com.example.speech.CTOPP.Subtest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MemoryForDigitsTest {

    public static void run() {
        List<String> digitSequences = loadData("/CTOPP/data/memory-digits.json");
        Scanner scanner = new Scanner(System.in);
        int total = 0, correct = 0;

        System.out.println("Memory for Digits Test");

        Collections.shuffle(digitSequences); //randomizes the order of the list
        List<String> selected = digitSequences.subList(0, Math.min(10, digitSequences.size())); //stores the first 10 list items in selected

        for (String sequence : selected) {
            System.out.printf("Listen to this sequence of digits and repeat it back:\n%s\n", sequence);
            System.out.print("Your answer: ");
            String response = scanner.nextLine().trim().replaceAll("\\s+", ""); //formats the user's response to remove whitespaces

            total++;
            if (response.equals(sequence)) {
                correct++;
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
