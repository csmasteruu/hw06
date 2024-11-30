import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EvilHangmanTest {

    @Test
    void testGetPattern() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple", "ample"));
        assertEquals("a____", game.getPattern("apple", 'a'), "Pattern should match guessed letter.");
        assertEquals("_pp__", game.getPattern("apple", 'p'), "Pattern should reflect repeated letters.");
        assertEquals("_____", game.getPattern("apple", 'z'), "Pattern should handle non-existent guessed letter.");
    }

    @Test
    void testPartition() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple", "ample", "actor"));
        Map<String, Set<String>> families = game.partition('p');

        assertEquals(3, families.size(), "Partition should have three family.");
        assertTrue(families.containsKey("_pp__"), "Partition should include '_pp__'.");
        assertTrue(families.containsKey("__p__"), "Partition should include '__p__'.");
        assertTrue(families.containsKey("_____"), "Partition should include '_____'.");
    }

    @Test
    void testSelectLargestFamily() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple", "ample", "actor"));
        Map<String, Set<String>> families = new HashMap<>();
        families.put("a____", new HashSet<>(Arrays.asList("apple", "ample")));
        families.put("_____", new HashSet<>(Arrays.asList("actor")));

        String largestFamily = game.selectLargestFamily(families);
        assertEquals("a____", largestFamily, "Largest family should be chosen based on size.");
    }

    @Test
    void testProcessGuessCorrect() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple", "ample"));
        game.processGuess('a');
        assertEquals("a____", game.solution.getPartialSolution(), "Solution should reflect correct guess.");
    }

    @Test
    void testProcessGuessIncorrect() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple", "ample"));
        game.processGuess('z');
        assertTrue(game.getIncorrectGuesses().contains('z'), "Incorrect guesses should include the guessed letter.");
    }

    @Test
    void testPromptForGuessValid() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple", "ample"));
        Scanner mockScanner = new Scanner("a\n");
        char guess = game.promptForGuess(mockScanner);
        assertEquals('a', guess, "Prompt for guess should return the entered letter.");
    }

    @Test
    void testPromptForGuessInvalid() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple", "ample"));
        Scanner mockScanner = new Scanner("1\nz\n");
        char guess = game.promptForGuess(mockScanner);
        assertEquals('z', guess, "Prompt for guess should skip invalid input.");
    }

    @Test
    void testWinConditionBeforeAll26Guesses() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple"));

        game.processGuess('a'); // Partial pattern: "a____"
        game.processGuess('p'); // Partial pattern: "app__"
        game.processGuess('l'); // Partial pattern: "appl_"
        game.processGuess('e'); // Complete word: "apple"

        assertTrue(game.solution.isSolved(), "The solution should be solved when all letters in the word are guessed.");
        assertEquals("apple", game.solution.getPartialSolution(), "The solution should match the word when solved.");

        Set<Character> previousGuesses = game.getPreviousGuesses();
        assertTrue(previousGuesses.size() < 26, "The game should end as soon as the word is solved.");
    }


    @Test
    void testVictoryCondition() {
        EvilHangman game = new EvilHangman(Arrays.asList("apple"));
        game.processGuess('a');
        game.processGuess('p');
        game.processGuess('l');
        game.processGuess('e');
        assertTrue(game.solution.isSolved(), "Game should be solved when all letters are guessed.");
        assertEquals("apple", game.solution.getPartialSolution(), "Solution should match the target word.");
    }

    @Test
    void testUpdateSolutionSingleLetter() {
        EvilSolution solution = new EvilSolution(5);
        solution.updateSolution("a____", 'a');
        assertEquals("a____", solution.getPartialSolution(), "Solution should reflect the guessed letter.");
    }

    @Test
    void testUpdateSolutionMultipleLetters() {
        EvilSolution solution = new EvilSolution(5);
        solution.updateSolution("_pp__", 'p');
        assertEquals("_pp__", solution.getPartialSolution(), "Solution should reflect multiple occurrences of the guessed letter.");
    }

    @Test
    void testUpdateSolutionNoChange() {
        EvilSolution solution = new EvilSolution(5);
        solution.updateSolution("_____", 'z');
        assertEquals("_____", solution.getPartialSolution(), "Solution should not change if guessed letter is not in the word.");
    }

    @Test
    void testIsSolved() {
        EvilSolution solution = new EvilSolution(5);
        solution.updateSolution("apple", 'a');
        solution.updateSolution("apple", 'p');
        solution.updateSolution("apple", 'l');
        solution.updateSolution("apple", 'e');
        assertTrue(solution.isSolved(), "Solution should be solved when all letters are guessed.");
    }

    @Test
    void testPrintProgress() {
        EvilSolution solution = new EvilSolution(5);
        solution.updateSolution("_pp__", 'p');
        solution.printProgress();
        assertEquals("_ p p _ _", solution.getPartialSolution().replaceAll("", " ").trim(), "Progress should match the partial solution.");
    }
}
