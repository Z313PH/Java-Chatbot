//package class_3000;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class Chatbot {
    private Map<String, String> qaLibrary = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Chatbot(String chatbotQA) {
        try (Scanner scanner = new Scanner(new File(chatbotQA))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("=>");
                if (parts.length == 2) {
                    qaLibrary.put(parts[0].trim().toLowerCase(), parts[1].trim());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + chatbotQA);
            e.printStackTrace();
        }
    }

    private int levenshteinDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public String getResponse(String userInput) throws InterruptedException, ExecutionException {
        String simplifiedInput = userInput.toLowerCase().trim();
        List<Future<Pair<Integer, String>>> futures = new ArrayList<>();

        for (String key : qaLibrary.keySet()) {
            final String questionKey = key;
            futures.add(executor.submit(() -> {
                int distance = levenshteinDistance(simplifiedInput, questionKey);
                return new Pair<>(distance, qaLibrary.get(questionKey));
            }));
        }

        String bestMatch = null;
        int lowestDistance = Integer.MAX_VALUE;

        for (Future<Pair<Integer, String>> future : futures) {
            Pair<Integer, String> result = future.get();
            if (result.getFirst() < lowestDistance) {
                bestMatch = result.getSecond();
                lowestDistance = result.getFirst();
            }
        }

        if (bestMatch != null && lowestDistance <= simplifiedInput.length() / 2) {
            return bestMatch;
        } else {
            return "I'm not sure how to answer that. Can you try asking differently?";
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final Chatbot chatbot = new Chatbot("/Users/eliewamana/Library/CloudStorage/OneDrive-SouthernUtahUniversity/School/Spring Semester 2024/CS-3000-01/Group Project/src/questions.txt");
        Scanner userInputScanner = new Scanner(System.in);

        System.out.println("Chatbot is online. Type 'exit' to end the conversation.");
        while (true) {
            System.out.print("You: ");
            String userInput = userInputScanner.nextLine();
            if ("exit".equalsIgnoreCase(userInput.trim())) {
                break;
            }
            String response = chatbot.getResponse(userInput);
            System.out.println("Chatbot: " + response);
        }
        System.out.println("Chatbot is now offline.");
        userInputScanner.close();
        chatbot.executor.shutdown();
    }
}

class Pair<T, U> {
    private T first;
    private U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
}










/*
public class Chatbot {
    private Map<String, String> qaLibrary = new HashMap<>();

    public Chatbot(String chatbotQA) {
        try (Scanner scanner = new Scanner(new File(chatbotQA))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("=>");
                if (parts.length == 2) {
                    qaLibrary.put(parts[0].trim().toLowerCase(), parts[1].trim());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + chatbotQA);
            e.printStackTrace();
        }
    }
    private int levenshteinDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public String getResponse(String userInput) {
        String simplifiedInput = userInput.toLowerCase().trim();
        String bestMatch = null;
        int lowestDistance = Integer.MAX_VALUE;

        for (String key : qaLibrary.keySet()) {
            int distance = levenshteinDistance(simplifiedInput, key);
            if (distance < lowestDistance) {
                bestMatch = qaLibrary.get(key);
                lowestDistance = distance;
            }
        }

        if (bestMatch != null && lowestDistance <= simplifiedInput.length() / 2) {
            return bestMatch;
        } else {
            return "I'm not sure how to answer that. Can you try asking differently?";
        }
    }

    public static void main(String[] args) {
        final Chatbot chatbot = new Chatbot("C:\\Users\\mckayfitzgerald\\Downloads\\questions.txt");
        Scanner userInputScanner = new Scanner(System.in);
        
        System.out.println("Chatbot is online. Type 'exit' to end the conversation.");
        while (true) {
            System.out.print("You: ");
            String userInput = userInputScanner.nextLine();
            if ("exit".equalsIgnoreCase(userInput.trim())) {
                break;
            }
            String response = chatbot.getResponse(userInput);
            System.out.println("Chatbot: " + response);
        }
        System.out.println("Chatbot is now offline.");
        userInputScanner.close();
    

       
}
}
*/