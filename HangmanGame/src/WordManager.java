import java.io.*;
import java.util.*;

public class WordManager {
    public static String getRandomWord() {
        List<String> words = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("words.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) words.add(line.toUpperCase());
            }
        } catch (IOException e) {
            words.add("ERROR"); // fallback
        }

        if (words.isEmpty()) words.add("JAVA");
        
        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);
        return words.get(rand.nextInt(words.size()));
    }
}
