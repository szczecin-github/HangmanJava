import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class HangmanGame extends JFrame {
    // --- FONTS & COLORS ---
    private final Color TEXT_GREEN = new Color(38, 84, 33);
    private final Color TEXT_RED = new Color(84, 23, 23);
    private final Font MAIN_FONT = new Font("Verdana", Font.BOLD, 18);
    private final Font TITLE_FONT = new Font("Verdana", Font.BOLD, 28);
    private final Font RESULT_FONT = new Font("Verdana", Font.BOLD, 36);

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JLayeredPane layeredPane;
    
    private WordLoader wordLoader;
    private GameLogic logic;
    
    private boolean isSoundOn = true;
    private boolean isMusicOn = false;

    // GUI Components
    private JLabel wordDisplay;
    private JPanel keyboardPanel;
    private DrawingPanel stickmanPanel;
    private JPanel resultScreen;
    private JLabel resultMessage;
    private JLabel resultFace;
    private JPanel settingsOverlay;
    private BackgroundPanel gameScreenBackground;

    public HangmanGame() {
        Assets.load();
        wordLoader = new WordLoader("hangman_words.txt");
        logic = new GameLogic();

        setTitle("Hangman Pro");
        setSize(480, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 480, 850);
        setContentPane(layeredPane);

        // 1. Main Game Layers & Settings Overlay
        mainPanel.setBounds(0, 0, 480, 850);
        mainPanel.add(createStartScreen(), "START");
        mainPanel.add(createCategoryScreen(), "CATEGORY");
        mainPanel.add(createGameScreen(), "GAME");
        mainPanel.add(createResultScreen(), "RESULT");
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        settingsOverlay = createSettingsOverlay();
        settingsOverlay.setVisible(false);
        layeredPane.add(settingsOverlay, JLayeredPane.PALETTE_LAYER);

        setVisible(true);
    }
    private JPanel createStartScreen() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (Assets.get("bg_teal") != null) {
                     g.drawImage(Assets.get("bg_teal"), 0, 0, getWidth(), getHeight(), null);
                } else {
                     g.setColor(new Color(114, 138, 133));
                     g.fillRect(0, 0, getWidth(), getHeight());
                }

                if (Assets.get("logo") != null) {
                    g.drawImage(Assets.get("logo"), 0, 0, getWidth(), getHeight(), null);
                }

                if (Assets.get("bg_curve") != null) {
                     g.drawImage(Assets.get("bg_curve"), 0, 600, 480, 250, null);
                }
            }
        };
        panel.setLayout(null); 

        JButton settingsBtn = createSettingsButton();
        settingsBtn.setBounds(390, 20, 60, 60);
        panel.add(settingsBtn);

        ImageButton playBtn = new ImageButton("PLAY", "btn_green", TEXT_GREEN);
        playBtn.setBounds(100, 500, 280, 80);
        playBtn.addActionListener(e -> cardLayout.show(mainPanel, "CATEGORY"));
        panel.add(playBtn);

        ImageButton quitBtn = new ImageButton("QUIT", "btn_red", TEXT_RED);
        quitBtn.setBounds(100, 600, 280, 80);
        quitBtn.addActionListener(e -> System.exit(0));
        panel.add(quitBtn);

        return panel;
    }

    private JPanel createCategoryScreen() {
        JPanel panel = new BackgroundPanel("bg_teal");
        panel.setLayout(null);

        JButton settingsBtn = createSettingsButton();
        settingsBtn.setBounds(390, 20, 60, 60);
        panel.add(settingsBtn);

        JLabel title = new JLabel("CHOOSE CATEGORY", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(38, 84, 33));
        title.setBounds(40, 30, 400, 50);
        panel.add(title);

        // Button List
        JPanel listContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        listContainer.setBounds(65, 150, 350, 600);
        listContainer.setOpaque(false);
        
        addCategoryButton(listContainer, "Animals", "cat_animals");
        addCategoryButton(listContainer, "Fruits", "cat_fruits");
        addCategoryButton(listContainer, "Countries", "cat_countries");
        addCategoryButton(listContainer, "Sports", "cat_sports");
        addCategoryButton(listContainer, "Technology", "cat_tech");

        JLabel pinkBox = new JLabel(getScaledIcon("panel_inner", 360, 650));
        pinkBox.setBounds(60, 100, 360, 650);
        
        panel.add(listContainer);
        panel.add(pinkBox);
        panel.setComponentZOrder(listContainer, 0);
        panel.setComponentZOrder(pinkBox, 1);

        return panel;
    }

    private void addCategoryButton(JPanel panel, String name, String imgName) {
        Color txtColor = Color.DARK_GRAY;
        if (imgName.equals("cat_sports")) txtColor = new Color(49, 62, 147);
        if (imgName.equals("cat_animals")) txtColor = new Color(89, 41, 41);

        ImageButton btn = new ImageButton(name, imgName, txtColor);
        btn.setFont(MAIN_FONT);
        btn.setPreferredSize(new Dimension(260, 60));
        btn.addActionListener(e -> startGame(name));
        panel.add(btn);
    }

    // --- SCREEN 3: GAME ---
    private JPanel createGameScreen() {
        gameScreenBackground = new BackgroundPanel("bg_teal"); 
        gameScreenBackground.setLayout(null);

        JButton settingsBtn = createSettingsButton();
        settingsBtn.setBounds(390, 20, 60, 60);
        gameScreenBackground.add(settingsBtn);

        JLabel title = new JLabel("HANGMAN GAME", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(38, 84, 33));
        title.setBounds(40, 20, 400, 50);
        gameScreenBackground.add(title);

        // THE DRAWING PANEL
        stickmanPanel = new DrawingPanel();
        stickmanPanel.setBounds(40, 80, 400, 350);
        stickmanPanel.setOpaque(false);
        gameScreenBackground.add(stickmanPanel);

        wordDisplay = new JLabel("_ _ _ _", SwingConstants.CENTER);
        wordDisplay.setFont(new Font("Monospaced", Font.BOLD, 40));
        wordDisplay.setBounds(40, 430, 400, 60);
        gameScreenBackground.add(wordDisplay);

        keyboardPanel = new JPanel(new GridLayout(4, 7, 5, 5));
        keyboardPanel.setBounds(20, 520, 420, 250);
        keyboardPanel.setOpaque(false);
        gameScreenBackground.add(keyboardPanel);

        JLabel curve = new JLabel(getScaledIcon("bg_curve", 480, 250));
        curve.setBounds(0, 600, 480, 250);
        gameScreenBackground.add(curve);
        gameScreenBackground.setComponentZOrder(curve, gameScreenBackground.getComponentCount()-1); 

        return gameScreenBackground;
    }

    // --- SCREEN 4: RESULT ---
    private JPanel createResultScreen() {
        resultScreen = new BackgroundPanel("bg_win");
        resultScreen.setLayout(null);

        JButton settingsBtn = createSettingsButton();
        settingsBtn.setBounds(390, 20, 60, 60);
        resultScreen.add(settingsBtn);

        JLabel title = new JLabel("HANGMAN GAME", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(30, 60, 30));
        title.setBounds(40, 20, 400, 50);
        resultScreen.add(title);

        resultMessage = new JLabel("YOU WON!", SwingConstants.CENTER);
        resultMessage.setFont(RESULT_FONT);
        resultMessage.setForeground(Color.WHITE);
        resultMessage.setBounds(40, 200, 400, 60);
        resultScreen.add(resultMessage);

        resultFace = new JLabel();
        resultFace.setBounds(190, 280, 100, 100);
        resultScreen.add(resultFace);

        ImageButton replayBtn = new ImageButton("REPLAY", "btn_green", TEXT_GREEN);
        replayBtn.setBounds(100, 450, 280, 80);
        replayBtn.addActionListener(e -> cardLayout.show(mainPanel, "CATEGORY"));
        resultScreen.add(replayBtn);

        ImageButton quitBtn = new ImageButton("QUIT", "btn_red", TEXT_RED);
        quitBtn.setBounds(100, 550, 280, 80);
        quitBtn.addActionListener(e -> System.exit(0));
        resultScreen.add(quitBtn);

        return resultScreen;
    }

    // --- SETTINGS OVERLAY ---
    private JPanel createSettingsOverlay() {
        JPanel overlay = new JPanel(null);
        overlay.setBounds(0, 0, 480, 850);
        overlay.setOpaque(false);

        JPanel blocker = new JPanel();
        blocker.setBackground(new Color(0, 0, 0, 150));
        blocker.setBounds(0, 0, 480, 850);
        
        JLabel boxBg = new JLabel(getScaledIcon("panel_settings", 400, 300));
        boxBg.setBounds(40, 250, 400, 300);
        
        JPanel content = new JPanel(null);
        content.setBounds(40, 250, 400, 300);
        content.setOpaque(false);

        JLabel title = new JLabel("SETTINGS");
        title.setFont(TITLE_FONT);
        title.setForeground(Color.BLACK);
        title.setBounds(30, 30, 200, 40);
        content.add(title);

        JLabel soundLbl = new JLabel("SOUND: ON");
        soundLbl.setFont(MAIN_FONT);
        soundLbl.setForeground(Color.GREEN);
        soundLbl.setBounds(30, 100, 200, 30);
        soundLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                isSoundOn = !isSoundOn;
                soundLbl.setText("SOUND: " + (isSoundOn ? "ON" : "OFF"));
                soundLbl.setForeground(isSoundOn ? Color.GREEN : Color.RED);
            }
        });
        content.add(soundLbl);

        JLabel musicLbl = new JLabel("MUSIC: OFF");
        musicLbl.setFont(MAIN_FONT);
        musicLbl.setForeground(Color.RED);
        musicLbl.setBounds(30, 150, 200, 30);
        musicLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                isMusicOn = !isMusicOn;
                musicLbl.setText("MUSIC: " + (isMusicOn ? "ON" : "OFF"));
                musicLbl.setForeground(isMusicOn ? Color.GREEN : Color.RED);
            }
        });
        content.add(musicLbl);

        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setBounds(100, 220, 200, 50);
        closeBtn.addActionListener(e -> settingsOverlay.setVisible(false));
        content.add(closeBtn);

        overlay.add(content);
        overlay.add(boxBg);
        overlay.add(blocker);
        
        overlay.setComponentZOrder(content, 0);
        overlay.setComponentZOrder(boxBg, 1);
        overlay.setComponentZOrder(blocker, 2);

        return overlay;
    }

    // --- GAME LOGIC ---
    private void startGame(String category) {
        String secret = wordLoader.getRandomWord(category);
        logic.startNewGame(secret);
        updateBoard();
        generateKeyboard();
        
        ((BackgroundPanel)gameScreenBackground).setBgImage("bg_teal");
        wordDisplay.setForeground(Color.BLACK);
        
        cardLayout.show(mainPanel, "GAME");
    }

    private void generateKeyboard() {
        keyboardPanel.removeAll();
        String keys = "QWERTYUIOPASDFGHJKLZXCVBNM";
        for (char c : keys.toCharArray()) {
            ImageButton kBtn = new ImageButton(String.valueOf(c), "key_up", Color.BLACK);
            kBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            kBtn.addActionListener(e -> {
                kBtn.setEnabled(false);
                kBtn.setImg("key_down");
                handleGuess(c);
            });
            keyboardPanel.add(kBtn);
        }
        keyboardPanel.revalidate();
        keyboardPanel.repaint();
    }

    private void handleGuess(char c) {
        logic.playGuess(c);
        updateBoard();
        if (logic.isWon()) showResult(true);
        else if (logic.isLost()) showResult(false);
    }

    private void showResult(boolean won) {
        ((BackgroundPanel)resultScreen).setBgImage(won ? "bg_win" : "bg_lose");
        resultMessage.setText(won ? "YOU WON!" : "YOU LOST!");
        if (!won) resultMessage.setText("Word: " + logic.getSecretWord());
        
        String faceKey = won ? "face_win" : "face_lose";
        resultFace.setIcon(getScaledIcon(faceKey, 100, 100));
        
        cardLayout.show(mainPanel, "RESULT");
    }

    private void updateBoard() {
        wordDisplay.setText(logic.getDisplayWord());
        stickmanPanel.repaint();
    }

    private JButton createSettingsButton() {
        JButton btn = new JButton(new ImageIcon(Assets.get("btn_settings")));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> settingsOverlay.setVisible(true));
        return btn;
    }

    // --- imagine scalnig ---
    private ImageIcon getScaledIcon(String key, int w, int h) {
        BufferedImage img = Assets.get(key);
        if (img == null) return null;
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    class BackgroundPanel extends JPanel {
        private String bgKey;
        public BackgroundPanel(String bgKey) { this.bgKey = bgKey; }
        public void setBgImage(String key) { this.bgKey = key; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (Assets.get(bgKey) != null) {
                g.drawImage(Assets.get(bgKey), 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // --- DRAWING PANEL ---
    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int m = logic.getMistakes(); 

            if (m >= 1) drawPiece(g, "line_1", 40, 300, 150, 10);
            if (m >= 2) drawPiece(g, "line_2", 110, 50, 10, 250);
            if (m >= 3) drawPiece(g, "line_3", 110, 50, 150, 10);
            if (m >= 4) drawPiece(g, "line_4", 120, 260, 30, 40);
            if (m >= 5) drawPiece(g, "line_5", 120, 60, 30, 30);
            if (m >= 6) drawPiece(g, "line_6", 230, 60, 10, 60);
            
            if (m >= 7) drawPiece(g, "hangman_head", 205, 120, 50, 50);
            if (m >= 8) drawPiece(g, "line_7", 228, 170, 5, 75);
            if (m >= 9) drawPiece(g, "line_8", 195, 180, 35, 5);
            if (m >= 10) drawPiece(g, "line_9", 232, 180, 35, 5);
            if (m >= 11) drawPiece(g, "line_10", 205, 240, 25, 50);
            if (m >= 12) drawPiece(g, "line_11", 230, 240, 25, 50);
        }
        
        private void drawPiece(Graphics g, String key, int x, int y, int w, int h) {
            if (Assets.get(key) != null) {
                g.drawImage(Assets.get(key), x, y, w, h, null);
            }
        }
    }

    class ImageButton extends JButton {
        private String imgKey;
        private Color txtColor;
        public ImageButton(String text, String imgKey, Color txtColor) {
            super(text);
            this.imgKey = imgKey;
            this.txtColor = txtColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(txtColor);
            setFont(MAIN_FONT);
            setHorizontalTextPosition(SwingConstants.CENTER);
        }
        public void setImg(String k) { this.imgKey = k; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            if (Assets.get(imgKey) != null) g.drawImage(Assets.get(imgKey), 0, 0, getWidth(), getHeight(), null);
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HangmanGame::new);
    }
}