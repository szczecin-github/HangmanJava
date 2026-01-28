import java.util.HashSet;
import java.util.Set;

public class GameLogic {
    private String secretWord;
    private Set<Character> guessedLetters;
    private int mistakes;
    private final int MAX_MISTAKES = 12; 

    public void startNewGame(String word) {
        this.secretWord = word.toUpperCase();
        this.guessedLetters = new HashSet<>();
        this.mistakes = 0;
    }

    public boolean playGuess(char letter) {
        if (guessedLetters.contains(letter)) return false;
        guessedLetters.add(letter);
        if (!secretWord.contains(String.valueOf(letter))) {
            mistakes++;
            return false;
        }
        return true;
    }

    public String getDisplayWord() {
        StringBuilder sb = new StringBuilder();
        for (char c : secretWord.toCharArray()) {
            if (guessedLetters.contains(c)) sb.append(c).append(" ");
            else sb.append("_ ");
        }
        return sb.toString().trim();
    }

    public boolean isWon() {
        for (char c : secretWord.toCharArray()) {
            if (!guessedLetters.contains(c)) return false;
        }
        return true;
    }
    public boolean isLost() { return mistakes >= MAX_MISTAKES; }
    public int getMistakes() { return mistakes; }
    public String getSecretWord() { return secretWord; }
}