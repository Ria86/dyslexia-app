package com.example.speech.CTOPP.Subtest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class RapidSymbolicNamingTest {

    public static void run() {
        List<List<String>> itemsList = loadData("/CTOPP/data/rapid-symbolic.json");
        Scanner scanner = new Scanner(System.in);
        int totalItems = 0;
        int correctCount = 0;

        System.out.println("\nRapid Symbolic Naming Test");
        System.out.println("You will see a list of symbols (letters/numbers). Name all aloud as quickly as possible.");
        System.out.println("Type your answer separated by spaces:");

        Collections.shuffle(itemsList);
        List<List<String>> selected = itemsList.subList(0, Math.min(10, itemsList.size()));

        long totalTime = 0;

        for (List<String> items : selected) {
            System.out.println("\nSymbols to name: " + String.join(" ", items));
            System.out.print("Press Enter when ready...");
            scanner.nextLine();

            long start = System.currentTimeMillis();
            System.out.print("Your answer: ");
            String response = scanner.nextLine().trim().toLowerCase();
            long end = System.currentTimeMillis();

            long timeTaken = end - start;
            totalTime += timeTaken;

            String[] spokenItems = response.split("\\s+");
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

        System.out.printf("%d/%d correct\n", correctCount, totalItems);
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
