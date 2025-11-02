public class GameLogic {
    private String word;
    private char[] display;
    private int stage;

    public GameLogic(String word) {
        this.word = word.toUpperCase();
        display = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            display[i] = (word.charAt(i) == ' ') ? ' ' : '_';
        }
        stage = 0;
    }

    public void guessLetter(char letter) {
        boolean correct = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                display[i] = letter;
                correct = true;
            }
        }
        if (!correct) stage++;
    }

    public String getDisplayedWord() {
        return new String(display);
    }

    public int getHangmanStage() {
        return stage;
    }

    public boolean isGameWon() {
        return new String(display).equals(word);
    }

    public boolean isGameOver() {
        return stage >= 6;
    }

    public String getWord() {
        return word;
    }
}
