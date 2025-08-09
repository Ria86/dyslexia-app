//running code: mvn exec:java -Dexec.mainClass="com.example.speech.CTOPP.CTOPPAssessment"


package com.example.speech.CTOPP;

import java.util.Scanner;

import com.example.speech.CTOPP.Subtest.BlendingTest;
import com.example.speech.CTOPP.Subtest.ElisionTest;
import com.example.speech.CTOPP.Subtest.MemoryForDigitsTest;
import com.example.speech.CTOPP.Subtest.PhonemeIsolationTest;
import com.example.speech.CTOPP.Subtest.RapidSymbolicNamingTest;
import com.example.speech.CTOPP.Subtest.SoundMatchingTest;

public class CTOPPAssessment {
    public static void main(String[] args) {

        int totalQuestions = 0;
        int correctAnswers = 0;
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to the CTOPP Assessment (CLI version)");
        System.out.println("You'll be asked phonological awareness questions. Answer as best you can.\n");


        //running the tests
        BlendingTest.run();
        ElisionTest.run();
        MemoryForDigitsTest.run();
        PhonemeIsolationTest.run();
        RapidSymbolicNamingTest.run();
        SoundMatchingTest.run();


        System.out.println("\nAssessment complete!");
        System.out.println("You got " + correctAnswers + " out of " + totalQuestions + " questions correct.");
        double percent = ((double) correctAnswers / totalQuestions) * 100;
        System.out.printf("Score: %.1f%%\n", percent);
    }
}
