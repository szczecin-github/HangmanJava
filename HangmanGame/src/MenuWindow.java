import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuWindow extends JFrame {
    public MenuWindow() {
        setTitle("Hangman - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLayout(new GridLayout(3, 1));

        JLabel title = new JLabel("Hangman", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));

        JButton soloButton = new JButton("Solo Mode");
        JButton pvpButton = new JButton("Player vs Player");

        soloButton.addActionListener(e -> {
            dispose();
            new GameWindow(true, null);
        });

        pvpButton.addActionListener(e -> {
            String customWord = JOptionPane.showInputDialog(this, "Enter a word for Player 2:");
            if (customWord != null && !customWord.isBlank()) {
                dispose();
                new GameWindow(false, customWord.trim().toUpperCase());
            }
        });

        add(title);
        add(soloButton);
        add(pvpButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
