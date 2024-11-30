import java.io.*;
import java.util.*;

public class EvilHangman {
    private Set<String> wordList;
    private Set<Character> previousGuesses;
    private Set<Character> incorrectGuesses;
    protected EvilSolution solution;

    public EvilHangman(String filename) {
        try {
            wordList = new HashSet<>(dictionaryToList(filename));
        } catch (IOException e) {
            System.out.printf(
                    "Couldn't read from the file %s. Verify that you have it in the right place and try running again.",
                    filename
            );
            e.printStackTrace();
            System.exit(0);
        }

        if (wordList.isEmpty()) {
            throw new IllegalArgumentException("Dictionary is empty or does not contain valid words.");
        }

        initializeGame();
    }

    // Constructor for testing
    public EvilHangman(List<String> mockDictionary) {
        if (mockDictionary == null || mockDictionary.isEmpty()) {
            throw new IllegalArgumentException("Dictionary is empty or null.");
        }
        wordList = new HashSet<>(mockDictionary);
        initializeGame();
    }

    private void initializeGame() {
        previousGuesses = new HashSet<>();
        incorrectGuesses = new TreeSet<>();
        int randomWordLength = wordList.iterator().next().length();
        solution = new EvilSolution(randomWordLength);
    }

    private List<String> dictionaryToList(String filename) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        }
        return words;
    }

    public Map<String, Set<String>> partition(char guess) {
        Map<String, Set<String>> families = new HashMap<>();
        for (String word : wordList) {
            String pattern = getPattern(word, guess);
            families.computeIfAbsent(pattern, k -> new HashSet<>()).add(word);
        }
        return families;
    }

    public String selectLargestFamily(Map<String, Set<String>> families) {
        return families.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().size()))
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("No families found"));
    }

    public String getPattern(String word, char guess) {
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

    public void processGuess(char guess) {
        if (previousGuesses.contains(guess)) {
            System.out.println("You've already guessed that letter!");
            return;
        }

        previousGuesses.add(guess);
        Map<String, Set<String>> families = partition(guess);
        String pattern = selectLargestFamily(families);

        if (families.isEmpty() || families.get(pattern).isEmpty()) {
            throw new IllegalStateException("No valid words left to guess.");
        }

        wordList = families.get(pattern);
        solution.updateSolution(pattern, guess);

        if (!pattern.contains(String.valueOf(guess))) {
            incorrectGuesses.add(guess);
        }
    }

    public char promptForGuess(Scanner scanner) {
        while (true) {
            System.out.print("Guess a letter: ");
            String input = scanner.nextLine().toLowerCase();
            if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
                System.out.println("Please enter a single valid letter.");
            } else {
                return input.charAt(0);
            }
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (!solution.isSolved() && previousGuesses.size() < 26) {
            solution.printProgress();
            System.out.println("Incorrect guesses: " + incorrectGuesses);
            System.out.println("Remaining guesses: " + (26 - previousGuesses.size()));
            char guess = promptForGuess(scanner);
            processGuess(guess);

            if (solution.isSolved()) {
                System.out.println("Congratulations! You've guessed the word: " + solution.getPartialSolution());
                return;
            }
        }

        if (previousGuesses.size() == 26) {
            System.out.println("You've used all 26 letters. Game over!");
            System.out.println("The correct word was: " + wordList.iterator().next());
        }
    }
        public Set<Character> getPreviousGuesses() {
            return new HashSet<>(previousGuesses);
        }

        public Set<Character> getIncorrectGuesses() {
            return new HashSet<>(incorrectGuesses);
        }

        }
