import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JFrame {
    private GameLogic logic;
    private JLabel wordLabel;
    private JLabel statusLabel;
    private JPanel buttonPanel;

    public GameWindow(boolean soloMode, String customWord) {
        setTitle("Hangman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        String word = soloMode ? WordManager.getRandomWord() : customWord.toUpperCase();
        logic = new GameLogic(word);

        wordLabel = new JLabel(logic.getDisplayedWord(), SwingConstants.CENTER);
        wordLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        wordLabel.setForeground(Color.BLUE);

        statusLabel = new JLabel("Hangman: " + logic.getHangmanStage() + "/6", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        buttonPanel = new JPanel(new GridLayout(3, 9, 5, 5));
        /*
        for (char c = 'A'; c <= 'Z'; c++) {
            JButton btn = new JButton(String.valueOf(c));
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.addActionListener(e -> handleGuess(btn, c));
            buttonPanel.add(btn);
        }
        */
        for (char c = 'A'; c <= 'Z'; c++) {
    JButton btn = new JButton(String.valueOf(c));
    final char letter = c;
    btn.addActionListener(e -> handleGuess(btn, letter));
    buttonPanel.add(btn);
}

        add(wordLabel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleGuess(JButton btn, char letter) {
        btn.setEnabled(false);
        logic.guessLetter(letter);

        wordLabel.setText(logic.getDisplayedWord());
        statusLabel.setText("Hangman: " + logic.getHangmanStage() + "/6");

        if (logic.isGameWon()) {
            endGame("You guessed it! The word was: " + logic.getWord());
        } else if (logic.isGameOver()) {
            endGame("Game Over! The word was: " + logic.getWord());
        }
    }

    private void endGame(String message) {
        JOptionPane.showMessageDialog(this, message);
        dispose();
        new MenuWindow();
    }
}
