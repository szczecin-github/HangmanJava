import java.io.*;
import java.util.*;

public class WordLoader {
    private Map<String, List<String>> wordData;

    public WordLoader(String filename) {
        wordData = new LinkedHashMap<>();
        loadFromFile(filename);
    }

    private void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String currentCategory = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("List of")) {
                    continue; 
                }

                // Strict Category Check & Everything else is a word

                if (isAllowedCategory(line)) {
                    currentCategory = capitalize(line);
                    wordData.putIfAbsent(currentCategory, new ArrayList<>());
                } 
                else if (currentCategory != null) {
                    wordData.get(currentCategory).add(line.toUpperCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private boolean isAllowedCategory(String line) {
        String s = line.toUpperCase();
        return s.equals("ANIMALS") || s.equals("FRUITS") || s.equals("COUNTRIES") || 
               s.equals("SPORTS") || s.equals("TECHNOLOGY");
    }

    public Set<String> getCategories() { return wordData.keySet(); }

    public String getRandomWord(String category) {
        List<String> words = wordData.get(category);
        if (words == null || words.isEmpty()) return "JAVA";
        return words.get(new Random().nextInt(words.size()));
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}