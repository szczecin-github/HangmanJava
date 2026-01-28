import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assets {
    private static Map<String, BufferedImage> images = new HashMap<>();

    public static void load() {
        loadImg("bg_teal", "assets/bg_teal.png");
        loadImg("bg_curve", "assets/bg_curve.png");
        loadImg("bg_win", "assets/bg_win.png");
        loadImg("bg_lose", "assets/bg_lose.png");
        loadImg("panel_inner", "assets/panel_inner.png");
        loadImg("panel_settings", "assets/panel_settings.png");
        loadImg("logo", "assets/logo.png");

        // --- quick fix, need more ducktape later ---
        loadImg("panel_settings", "assets/panel_settings.png");

        loadImg("btn_green", "assets/btn_green.png");
        loadImg("btn_red", "assets/btn_red.png");
        loadImg("btn_settings", "assets/btn_settings.png");

        loadImg("cat_animals", "assets/cat_animals.png");
        loadImg("cat_fruits", "assets/cat_fruits.png");
        loadImg("cat_countries", "assets/cat_countries.png");
        loadImg("cat_sports", "assets/cat_sports.png");
        loadImg("cat_tech", "assets/cat_tech.png");

        loadImg("key_up", "assets/key_up.png");
        loadImg("key_down", "assets/key_down.png");

        // --- Stickman Parts ---
        loadImg("hangman_head", "assets/hangman_head.png"); 
        loadImg("face_win", "assets/face_win.png");
        loadImg("face_lose", "assets/face_lose.png");


        for (int i = 1; i <= 11; i++) {
            loadImg("line_" + i, "assets/line_" + i + ".png");
        }
    }

    private static void loadImg(String key, String path) {
        try {
            images.put(key, ImageIO.read(new File(path)));
        } catch (IOException e) {
            System.err.println("Could not load: " + path);
        }
    }

    public static BufferedImage get(String key) {
        return images.get(key);
    }
}