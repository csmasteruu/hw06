import java.util.*;

public class EvilSolution {
    private String partialSolution;
    private int remainingLetters;

    public EvilSolution(int wordLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            sb.append("_");
        }
        partialSolution = sb.toString();
        remainingLetters = wordLength;
    }

    public String getPartialSolution() {
        return partialSolution;
    }

    public boolean isSolved() {
        return remainingLetters == 0;
    }

    public void updateSolution(String pattern, char guess) {
        StringBuilder updatedSolution = new StringBuilder(partialSolution);

        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == guess) {
                updatedSolution.setCharAt(i, guess);
                remainingLetters--;
            }
        }

        partialSolution = updatedSolution.toString();
    }

    public void printProgress() {
        for (char c : partialSolution.toCharArray()) {
            System.out.print(c + " ");
        }
        System.out.println();
    }
}