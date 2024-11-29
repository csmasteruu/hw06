import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EvilHangmanTest {

    private EvilHangman evilHangman;
    private List<String> Dictionary;

    @BeforeEach
    void setUp() {
        List<String> mockDictionary = Arrays.asList("apple", "angle", "amble");
        Scanner mockScanner = new Scanner(System.in);
        evilHangman = new EvilHangman(mockDictionary, mockScanner);
    }

    @Test
    void testSetupWithValidDictionary() {
        List<String> mockDictionary = Arrays.asList("apple", "angle", "amble");
        EvilHangman evilHangman = new EvilHangman(mockDictionary, new Scanner(System.in));
        assertEquals(new HashSet<>(mockDictionary), evilHangman.getWordList(), "Word list should be initialized with the dictionary.");
        assertEquals(mockDictionary.get(0).length(), evilHangman.getSolution().getPartialSolution().length(), "Solution length should match the word length.");
    }

    @Test
    void testSetupWithEmptyDictionary() {
        List<String> emptyDictionary = Collections.emptyList();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new EvilHangman(emptyDictionary, new Scanner(System.in)));
        assertEquals("Dictionary is empty or null.", exception.getMessage(), "Exception should be thrown for an empty dictionary.");
    }

    @Test
    void testSetupWithNullDictionary() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new EvilHangman(null, new Scanner(System.in)));
        assertEquals("Dictionary is empty or null.", exception.getMessage(), "Exception should be thrown for a null dictionary.");
    }

    @Test
    void testRandomWordSelection() {
        List<String> mockDictionary = Arrays.asList("apple", "angle", "amble");
        EvilHangman evilHangman = new EvilHangman(mockDictionary, new Scanner(System.in));

        // Check that the solution's length corresponds to a word in the dictionary
        String partialSolution = evilHangman.getSolution().getPartialSolution();
        assertTrue(mockDictionary.stream().anyMatch(word -> word.length() == partialSolution.length()), "Selected word length should match the solution.");
    }

    @Test
    void testPartition() {
        // Setup initial state
        char guess = 'a';
        Map<String, Set<String>> result = evilHangman.partition(guess);

        // Assert the correct families are created
        assertTrue(result.containsKey("a____"));
        assertTrue(result.containsKey("_____"));
        assertEquals(Set.of("apple", "angle", "amble", "ample"), result.get("a____"));
    }

    @Test
    void testPromptForGuessValidInput() {
        // Simulate user input "a\n"
        InputStream inputStream = new ByteArrayInputStream("a\n".getBytes());
        Scanner mockScanner = new Scanner(inputStream);
        List<String> mockDictionary = Arrays.asList("apple", "angle", "amble");
        EvilHangman evilHangman = new EvilHangman(mockDictionary, mockScanner);

        char guess = evilHangman.promptForGuess();
        assertEquals('a', guess, "The method should return the guessed character.");
    }

    @Test
    void testPromptForGuessInvalidInput() {
        // Simulate user input: "1\nabc\n@\na\n"
        InputStream inputStream = new ByteArrayInputStream("1\nabc\n@\na\n".getBytes());
        Scanner mockScanner = new Scanner(inputStream);
        List<String> mockDictionary = Arrays.asList("apple", "angle", "amble");
        EvilHangman evilHangman = new EvilHangman(mockDictionary, mockScanner);

        char guess = evilHangman.promptForGuess();
        assertEquals('a', guess, "The method should ignore invalid inputs and return the first valid guess.");
    }

    @Test
    void testPromptForGuessRepeatedInput() {
        // Simulate user input: "a\nb\na\n"
        InputStream inputStream = new ByteArrayInputStream("a\nb\na\n".getBytes());
        Scanner mockScanner = new Scanner(inputStream);
        List<String> mockDictionary = Arrays.asList("apple", "angle", "amble");
        EvilHangman evilHangman = new EvilHangman(mockDictionary, mockScanner);
        evilHangman.processGuess('a');
        char guess = evilHangman.promptForGuess();
        assertEquals('b', guess, "The method should ignore repeated guesses and return a new valid guess.");
    }

    @Test
    void testSelectLargestFamily() {
        Map<String, Set<String>> families = new HashMap<>();
        families.put("a____", Set.of("apple", "angle"));
        families.put("_____l", Set.of("amble"));
        families.put("a__e_", Set.of("ample"));
        String largestFamily = evilHangman.selectLargestFamily(families);
        assertEquals("a____", largestFamily);
    }

    @Test
    void testGetPattern() {
        // Check pattern generation
        String pattern = evilHangman.getPattern("apple", 'p');
        assertEquals("_pp__", pattern);

        pattern = evilHangman.getPattern("actor", 'x');
        assertEquals("_____", pattern);
    }

    @Test
    void testProcessGuess() {
        // Simulate a guess
        char guess = 'a';
        evilHangman.processGuess(guess);

        assertTrue(evilHangman.getPreviousGuesses().contains(guess));
        assertFalse(evilHangman.getIncorrectGuesses().contains(guess));
    }

    @Test
    void testEvilSolutionUpdate() {
        EvilSolution solution = new EvilSolution(5);
        solution.updateSolution("_pp__", 'p');
        assertEquals("_pp__", solution.getPartialSolution(), "Solution should be '_pp__' after guessing 'p'.");

        // Guess 'e' to reveal the last letter
        solution.updateSolution("_pp_e", 'e');
        assertEquals("_pp_e", solution.getPartialSolution(), "Solution should be '_pp_e' after guessing 'e'.");

        // Guess 'a' to reveal the first letter
        solution.updateSolution("apple", 'a');
        assertEquals("apple", solution.getPartialSolution(), "Solution should be 'apple' after guessing 'a'.");
        assertTrue(solution.isSolved(), "Solution should be solved when all letters are revealed.");
    }
}