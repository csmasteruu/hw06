import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Random;
import java.util.Scanner;

public class EvilHangman {
    private Set<String> wordList;
    private Set<Character> previousGuesses;
    private TreeSet<Character> incorrectGuesses;
    private EvilSolution solution;
    private Scanner inputScanner;

    public EvilHangman(String filename) {
        try {
            wordList = new HashSet<>(dictionaryToList(filename));
        } catch (IOException e) {
            System.out.printf(
                    "Couldn't read from the file %s. Verify that you have it in the right place and try running again.",
                    filename);
            e.printStackTrace();
            System.exit(0);
        }

        if (wordList.isEmpty()) {
            throw new IllegalArgumentException("Dictionary is empty or does not contain words of the expected length.");
        }

        previousGuesses = new HashSet<>();
        incorrectGuesses = new TreeSet<>();

        int randomIndex = new Random().nextInt(wordList.size());
        String targetWord = new ArrayList<>(wordList).get(randomIndex);

        solution = new EvilSolution(targetWord.length());
        inputScanner = new Scanner(System.in);
    }

    public void start() {
        while (!solution.isSolved()) {
            char guess = promptForGuess();
            processGuess(guess);
        }
        printVictory();
    }

    private char promptForGuess() {
        while (true) {
            System.out.println("Guess a letter.\n");
            solution.printProgress();
            System.out.println("Incorrect guesses:\n" + incorrectGuesses.toString());
            String input = inputScanner.next();
            if (input.length() != 1) {
                System.out.println("Please enter a single character.");
            } else if (! Character.isLetter(input.charAt(0))){
                System.out.println("Please enter a valid letter.");
            } else if (previousGuesses.contains(input.charAt(0))) {
                System.out.println("You've already guessed that.");
            } else {
                return input.charAt(0);
            }
        }
    }

    private void processGuess(char guess) {
        previousGuesses.add(guess);
        Map<String, Set<String>> families = partition(guess);
        String pattern = selectLargestFamily(families);
        if (wordList.isEmpty()) {
            throw new IllegalStateException("No valid words left to guess.");
        }
        wordList = families.get(pattern);
        solution.updateSolution(pattern, guess);
        if (!pattern.contains(String.valueOf(guess))) {
            incorrectGuesses.add(guess);
        }
    }

    private Map<String, Set<String>> partition(char guess) {
        Map<String, Set<String>> families = new HashMap<>();
        if (wordList.isEmpty()) {
            throw new IllegalStateException("Word list is empty. Cannot partition.");
        }
        for (String word : wordList) {
            String pattern = getPattern(word, guess);
            if (!families.containsKey(pattern)) {
                families.put(pattern, new HashSet<>());
            }
            families.get(pattern).add(word);
        }
        return families;
    }

    private String getPattern(String word, char guess) {
        StringBuilder pattern = new StringBuilder();
        for (char c : word.toCharArray()) {
            if (c == guess) {
                pattern.append(c);
            } else {
                pattern.append('_');
            }
        }
        return pattern.toString();
    }

    private String selectLargestFamily(Map<String, Set<String>> families) {
        String largestPattern = null;
        int maxSize = 0;

        for (String pattern : families.keySet()) {
            Set<String> words = families.get(pattern);
            int size = words.size();
            if (size > maxSize) {
                maxSize = size;
                largestPattern = pattern;
            }
        }

        if (largestPattern == null) {
            throw new RuntimeException("No families found.");
        }

        return largestPattern;
    }

    private void printVictory() {
        System.out.printf("Congrats! The word was %s%n", solution.getPartialSolution());
    }

    private static List<String> dictionaryToList(String filename) throws IOException {
        FileInputStream fs = new FileInputStream(filename);
        Scanner scnr = new Scanner(fs);

        List<String> wordList = new ArrayList<>();
        while (scnr.hasNext()) {
            wordList.add(scnr.next().toLowerCase());
        }

        return wordList;
    }
}
